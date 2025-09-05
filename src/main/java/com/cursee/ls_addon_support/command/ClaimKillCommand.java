package com.cursee.ls_addon_support.command;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.server;
import static com.cursee.ls_addon_support.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClaimKillCommand {

  public static boolean isAllowed() {
    return currentSeason.getSeason() != Seasons.UNASSIGNED;
  }

  public static boolean checkBanned(ServerCommandSource source) {
      if (isAllowed()) {
          return false;
      }
    source.sendError(Text.of("This command is only available when you have selected a Season."));
    return true;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {
    dispatcher.register(
        literal("claimkill")
            .then(argument("player", EntityArgumentType.player())
                .suggests(
                    (context, builder) -> CommandSource.suggestMatching(getSuggestions(), builder))
                .executes(context -> claimCredit(
                    context.getSource(), EntityArgumentType.getPlayer(context, "player")
                ))
            )
            .then(literal("validate")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("killer", EntityArgumentType.player())
                    .then(argument("victim", EntityArgumentType.player())
                        .executes(context -> claimCreditAccept(
                            context.getSource(),
                            EntityArgumentType.getPlayer(context, "killer"),
                            EntityArgumentType.getPlayer(context, "victim")
                        ))
                    )
                )
            )
    );
  }

  public static List<String> getSuggestions() {
      if (server == null) {
          return new ArrayList<>();
      }
    List<String> suggestions = new ArrayList<>();
    Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
    for (UUID uuid : recentDeaths) {
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
        if (player == null) {
            continue;
        }
      suggestions.add(player.getNameForScoreboard());
    }
    return suggestions;
  }

  public static int claimCredit(ServerCommandSource source, ServerPlayerEntity victim) {
      if (checkBanned(source)) {
          return -1;
      }
      if (victim == null) {
          return -1;
      }
    PlayerEntity player = source.getPlayer();
      if (player == null) {
          return -1;
      }

    Set<UUID> recentDeaths = currentSession.playerNaturalDeathLog.keySet();
    UUID victimUUID = victim.getUuid();
    if (!recentDeaths.contains(victimUUID)) {
      source.sendError(TextUtils.formatPlain(
          "{} did not die in the last 2 minutes. Or they might have been killed by a player directly.",
          victim));
      return -1;
    }
    if (player == victim) {
      source.sendError(Text.of("You cannot claim credit for your own death :P"));
      return -1;
    }
    Text textAll = TextUtils.format(
        "{}§7 claims credit for {}§7's death. Only an admin can validate this claim.", player,
        victim);
    PlayerUtils.broadcastMessage(textAll);
    String validateCommand = TextUtils.formatString("/claimkill validate {} {}", player, victim);
    Text adminText = Text.literal("§7Click ").append(
        Text.literal("here")
            .styled(style -> style
                .withColor(Formatting.BLUE)
                .withClickEvent(TextUtils.runCommandClickEvent(validateCommand))
                .withUnderline(true)
            )).append(Text.of("§7 to accept the claim if you think it's valid."));
    PlayerUtils.broadcastMessageToAdmins(adminText, 120);

    return 1;
  }

  public static int claimCreditAccept(ServerCommandSource source, ServerPlayerEntity killer,
      ServerPlayerEntity victim) {
      if (checkBanned(source)) {
          return -1;
      }
      if (killer == null) {
          return -1;
      }
      if (victim == null) {
          return -1;
      }

    Text message = TextUtils.format("{}§7's kill claim on {}§7 was accepted.", killer, victim);
    PlayerUtils.broadcastMessage(message);
    currentSeason.onClaimKill(killer, victim);
    currentSession.playerNaturalDeathLog.remove(victim.getUuid());

    return 1;
  }
}
