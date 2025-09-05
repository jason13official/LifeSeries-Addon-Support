package com.cursee.ls_addon_support.seasons.boogeyman;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BoogeymanCommand {

  public static boolean isAllowed() {
    return getBM().BOOGEYMAN_ENABLED;
  }

  public static boolean checkBanned(ServerCommandSource source) {
      if (isAllowed()) {
          return false;
      }
    source.sendError(Text.of(
        "This command is only available when the boogeyman has been enabled in the Life Series config."));
    return true;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {
    dispatcher.register(
        literal("boogeyman")
            .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
            .then(literal("clear")
                .executes(context -> boogeyClear(
                    context.getSource()
                ))
            )
            .then(literal("list")
                .executes(context -> boogeyList(
                    context.getSource()
                ))
            )
            .then(literal("count")
                .executes(context -> boogeyCount(
                    context.getSource()
                ))
            )
            .then(literal("add")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> addBoogey(context.getSource(),
                        EntityArgumentType.getPlayer(context, "player")))
                )
            )
            .then(literal("remove")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> removeBoogey(context.getSource(),
                        EntityArgumentType.getPlayer(context, "player")))
                )
            )
            .then(literal("cure")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> cureBoogey(context.getSource(),
                        EntityArgumentType.getPlayer(context, "player")))
                )
            )
            .then(literal("fail")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> failBoogey(context.getSource(),
                        EntityArgumentType.getPlayer(context, "player")))
                )
            )
            .then(literal("chooseRandom")
                .executes(context -> boogeyChooseRandom(
                    context.getSource()
                ))
            )

    );
  }

  public static BoogeymanManager getBM() {
    return currentSeason.boogeymanManager;
  }

  public static int failBoogey(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }
      if (target == null) {
          return -1;
      }

    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    if (!bm.isBoogeyman(target)) {
      source.sendError(Text.of("That player is not a Boogeyman"));
      return -1;
    }
    if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("§7Failing Boogeyman for {}§7...", target));
    }
    bm.playerFailBoogeymanManually(target);

    return 1;
  }

  public static int cureBoogey(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }
      if (target == null) {
          return -1;
      }

    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    if (!bm.isBoogeyman(target)) {
      source.sendError(Text.of("That player is not a Boogeyman"));
      return -1;
    }
    bm.cure(target);

    if (!bm.BOOGEYMAN_ANNOUNCE_OUTCOME) {
      OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Curing {}§7...", target));
    }

    return 1;
  }

  public static int addBoogey(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }

      if (target == null) {
          return -1;
      }

    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    if (bm.isBoogeyman(target)) {
      source.sendError(Text.of("That player is already a Boogeyman"));
      return -1;
    }
    bm.addBoogeymanManually(target);

    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is now a Boogeyman", target));
    return 1;
  }

  public static int removeBoogey(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }

      if (target == null) {
          return -1;
      }

    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    if (!bm.isBoogeyman(target)) {
      source.sendError(Text.of("That player is not a Boogeyman"));
      return -1;
    }
    bm.removeBoogeymanManually(target);

    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} is no longer a Boogeyman", target));
    return 1;
  }

  public static int boogeyList(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    List<String> allBoogeymen = new ArrayList<>();
    List<String> curedBoogeymen = new ArrayList<>();
    List<String> failedBoogeymen = new ArrayList<>();
    for (Boogeyman boogeyman : bm.boogeymen) {
      if (boogeyman.cured) {
        curedBoogeymen.add(boogeyman.name);
      } else if (boogeyman.failed) {
        failedBoogeymen.add(boogeyman.name);
      } else {
        allBoogeymen.add(boogeyman.name);
      }
    }

      if (allBoogeymen.isEmpty()) {
          allBoogeymen.add("§7None");
      }
      if (curedBoogeymen.isEmpty()) {
          curedBoogeymen.add("§7None");
      }
      if (failedBoogeymen.isEmpty()) {
          failedBoogeymen.add("§7None");
      }

    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Remaining Boogeymen: {}", allBoogeymen));
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Cured Boogeymen: {}", curedBoogeymen));
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Failed Boogeymen: {}", failedBoogeymen));
    return 1;
  }

  public static int boogeyCount(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    List<String> allBoogeymen = new ArrayList<>();
    List<String> curedBoogeymen = new ArrayList<>();
    List<String> failedBoogeymen = new ArrayList<>();
    for (Boogeyman boogeyman : bm.boogeymen) {
      if (boogeyman.cured) {
        curedBoogeymen.add(boogeyman.name);
      } else if (boogeyman.failed) {
        failedBoogeymen.add(boogeyman.name);
      } else {
        allBoogeymen.add(boogeyman.name);
      }
    }

    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Remaining Boogeymen: {}", allBoogeymen.size()));
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Cured Boogeymen: {}", curedBoogeymen.size()));
    OtherUtils.sendCommandFeedbackQuiet(source,
        TextUtils.format("Failed Boogeymen: {}", failedBoogeymen.size()));
    return 1;
  }

  public static int boogeyClear(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    bm.resetBoogeymen();
    OtherUtils.sendCommandFeedback(source, Text.of("All Boogeymen have been cleared"));
    return 1;
  }

  public static int boogeyChooseRandom(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    BoogeymanManager bm = getBM();
      if (bm == null) {
          return -1;
      }

    OtherUtils.sendCommandFeedback(source, Text.of("§7Choosing random Boogeymen..."));

    bm.resetBoogeymen();
    bm.prepareToChooseBoogeymen();

    return 1;
  }
}
