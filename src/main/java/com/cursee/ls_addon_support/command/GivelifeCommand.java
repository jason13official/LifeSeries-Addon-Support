package com.cursee.ls_addon_support.command;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.world.AnimationUtils;
import com.mojang.brigadier.CommandDispatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GivelifeCommand {

  private static final Map<UUID, Map<UUID, Long>> soulmateGivelifeRequests = new HashMap<>();

  public static boolean isAllowed() {
      if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
          return false;
      }
    return seasonConfig.GIVELIFE_COMMAND_ENABLED.get(seasonConfig);
  }

  public static boolean checkBanned(ServerCommandSource source) {
      if (isAllowed()) {
          return false;
      }
    source.sendError(Text.of("This command is not available."));
    return true;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {
    dispatcher.register(
        literal("givelife")
            .then(argument("player", EntityArgumentType.player())
                .executes(context -> giftLife(context.getSource(),
                    EntityArgumentType.getPlayer(context, "player")))
            )
    );
  }

  public static int giftLife(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }

    final ServerPlayerEntity self = source.getPlayer();
      if (self == null) {
          return -1;
      }
      if (target == null) {
          return -1;
      }
    if (!livesManager.isAlive(self)) {
      source.sendError(Text.of("You do not have any lives to give"));
      return -1;
    }
    boolean isRevive = !livesManager.isAlive(target);
    if (!Season.GIVELIFE_CAN_REVIVE && isRevive) {
      source.sendError(Text.of("That player is not alive"));
      return -1;
    }
    if (target == self) {
      source.sendError(Text.of("You cannot give a life to yourself"));
      return -1;
    }
    Integer currentLives = livesManager.getPlayerLives(self);
    if (currentLives == null || currentLives <= 1) {
      source.sendError(Text.of("You cannot give away your last life"));
      return -1;
    }
    Integer targetLives = livesManager.getPlayerLives(target);
    if (targetLives == null || targetLives >= currentSeason.GIVELIFE_MAX_LIVES) {
      source.sendError(Text.of("That player cannot receive any more lives"));
      return -1;
    }
    if (currentSeason instanceof DoubleLife doubleLife) {
      ServerPlayerEntity soulmate = doubleLife.getSoulmate(self);
      if (soulmate != null) {
        if (soulmate.equals(target)) {
          source.sendError(Text.of("You cannot give a life to your soulmate"));
          return -1;
        }
        boolean success = doubleLifeGiveLife(source, self, soulmate, target);
        if (!success) {
          return -1;
        }
      }
    }

    Text currentPlayerName = self.getStyledDisplayName();
    livesManager.removePlayerLife(self);
    livesManager.addToLifeNoUpdate(target);
    AnimationUtils.playTotemAnimation(self);
    TaskScheduler.scheduleTask(40,
        () -> livesManager.receiveLifeFromOtherPlayer(currentPlayerName, target, isRevive));

    if (currentSeason instanceof DoubleLife doubleLife) {
      doubleLife.syncSoulboundLives(self);
    }

    return 1;
  }

  public static boolean doubleLifeGiveLife(ServerCommandSource source, ServerPlayerEntity self,
      ServerPlayerEntity soulmate, ServerPlayerEntity target) {
    // Check if there already is a request from the soulmate for this target.
    Map<UUID, Long> soulmateRequest = soulmateGivelifeRequests.get(soulmate.getUuid());
    if (soulmateRequest != null) {
      for (Map.Entry<UUID, Long> entry : soulmateRequest.entrySet()) {
        UUID soulmateRequestTarget = entry.getKey();
        long requestTime = entry.getValue();
        if (soulmateRequestTarget.equals(target.getUuid()) && (
            System.currentTimeMillis() - requestTime <= 60000)) {
          // If the soulmate already requested a life for this target within the last 60s, give the life.
          soulmateRequest.remove(soulmateRequestTarget);
          return true;
        }
      }
    }

    // Add the request for this player
    if (soulmateGivelifeRequests.containsKey(self.getUuid())) {
      soulmateGivelifeRequests.get(self.getUuid())
          .put(target.getUuid(), System.currentTimeMillis());
    } else {
      Map<UUID, Long> request = new HashMap<>();
      request.put(target.getUuid(), System.currentTimeMillis());
      soulmateGivelifeRequests.put(self.getUuid(), request);
    }
    OtherUtils.sendCommandFeedbackQuiet(source,
        Text.of("§7Your soulmate must accept your request to give a life to this player."));
    soulmate.sendMessage(
        TextUtils.format("Your soulmate wants to give a life to {}.\nClick ", target)
            .append(
                Text.literal("here")
                    .styled(style -> style
                        .withColor(Formatting.BLUE)
                        .withClickEvent(TextUtils.runCommandClickEvent(
                            TextUtils.formatString("/givelife {}", target.getNameForScoreboard())))
                        .withUnderline(true)
                    )
            )
            .append(Text.of(" to accept the request."))
    );
    return false;
  }
}
