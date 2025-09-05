package com.cursee.ls_addon_support.command;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.cursee.ls_addon_support.seasons.other.LivesManager;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.seasons.season.lastlife.LastLifeLivesManager;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public class LivesCommand {

  public static boolean isAllowed() {
    return currentSeason.getSeason() != Seasons.UNASSIGNED;
  }

  public static boolean isAllowedNormal() {
    return isAllowed() && isNormalLife();
  }

  public static boolean isAllowedLimited() {
    return isAllowed() && !isNormalLife();
  }

  public static boolean isNormalLife() {
    return currentSeason.getSeason() != Seasons.LIMITED_LIFE;
  }

  public static boolean isLastLife() {
    return currentSeason.getSeason() == Seasons.LAST_LIFE;
  }

  public static boolean checkBanned(ServerCommandSource source) {
      if (isAllowed()) {
          return false;
      }
    source.sendError(Text.of("This command is not available when you have not selected a season."));
    return true;
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {

    dispatcher.register(
        literal("lives")
            .requires(source -> isAllowed())
            .executes(context -> showLives(context.getSource()))
            .then(literal("reload")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .executes(context -> reloadLives(
                    context.getSource())
                )
            )
            .then(literal("add")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgumentType.getPlayers(context, "player"), 1,
                        false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                            List.of("30m", "1h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), false, false)
                        )
                    )
                )
            )
            .then(literal("remove")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgumentType.getPlayers(context, "player"), -1,
                        false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            -IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                            List.of("30m", "1h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), false, true)
                        )
                    )
                )
            )
            .then(literal("set")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .then(argument("amount", IntegerArgumentType.integer(0))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            IntegerArgumentType.getInteger(context, "amount"), true)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                            List.of("8h", "16h", "24h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), true, false)
                        )
                    )
                )
            )
            .then(literal("get")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> getLivesFor(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                    )
                )
                .then(literal("*")
                    .executes(context -> getAllLives(
                        context.getSource())
                    )
                )
            )
            .then(literal("reset")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .executes(context -> resetLives(
                        context.getSource(), EntityArgumentType.getPlayers(context, "player"))
                    )
                )
            )
            .then(literal("resetAll")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .executes(context -> resetAllLives(
                    context.getSource())
                )
            )
            .then(literal("rollLives")
                .requires(
                    source -> isLastLife() && (isAdmin(source.getPlayer()) || (source.getEntity()
                        == null)))
                .executes(context -> assignRandomLives(
                    context.getSource(), PlayerUtils.getAllPlayers()
                ))
                .then(argument("players", EntityArgumentType.players())
                    .executes(context -> assignRandomLives(
                        context.getSource(), EntityArgumentType.getPlayers(context, "players")
                    ))
                )
            )
    );
  }

  public static int showLives(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    boolean normalLife = isNormalLife();

    ServerPlayerEntity self = source.getPlayer();

      if (self == null) {
          return -1;
      }
    if (!livesManager.hasAssignedLives(self)) {
      String timeOrLives = normalLife ? "lives" : "time";
      OtherUtils.sendCommandFeedbackQuiet(source,
          TextUtils.format("You have not been assigned any {} yet", timeOrLives));
      return 1;
    }

    Integer playerLives = livesManager.getPlayerLives(self);

    if (normalLife) {
      OtherUtils.sendCommandFeedbackQuiet(source,
          TextUtils.format("You have {} {}", livesManager.getFormattedLives(playerLives),
              TextUtils.pluralize("life", "lives", playerLives)));
    } else {
      OtherUtils.sendCommandFeedbackQuiet(source,
          TextUtils.format("You have {} left", livesManager.getFormattedLives(playerLives)));
    }

    if (playerLives == null || playerLives <= 0) {
      OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Womp womp."));
    }

    return 1;
  }

  public static int getAllLives(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "time";

    if (!ScoreboardUtils.existsObjective(LivesManager.SCOREBOARD_NAME)) {
      source.sendError(TextUtils.format("Nobody has been assigned {} yet", timeOrLives));
      return -1;
    }

    Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
    if (entries.isEmpty()) {
      source.sendError(TextUtils.format("Nobody has been assigned {} yet", timeOrLives));
      return -1;
    }
    String timeOrLives2 = normalLife ? "Lives" : "Times";

    MutableText text = TextUtils.format("Assigned {}: \n", timeOrLives2);
    for (ScoreboardEntry entry : entries) {
      String name = entry.owner();
        if (name.startsWith("`")) {
            continue;
        }
      int lives = entry.value();
      Formatting color = livesManager.getColorForLives(lives);
      if (normalLife) {
        text.append(TextUtils.format("{} has {} {}\n", Text.literal(name).formatted(color),
            livesManager.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
      } else {
        text.append(TextUtils.format("{} has {} left\n", Text.literal(name).formatted(color),
            livesManager.getFormattedLives(lives)));
      }
    }

    OtherUtils.sendCommandFeedbackQuiet(source, text);
    return 1;
  }

  public static int getLivesFor(ServerCommandSource source, ServerPlayerEntity target) {
      if (checkBanned(source)) {
          return -1;
      }
      if (target == null) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "time";

    if (!livesManager.hasAssignedLives(target)) {
      source.sendError(
          TextUtils.formatPlain("{} has not been assigned any {}", target, timeOrLives));
      return -1;
    }
    Integer lives = livesManager.getPlayerLives(target);
    if (normalLife) {
      OtherUtils.sendCommandFeedbackQuiet(source,
          TextUtils.format("{} has {} {}", target, livesManager.getFormattedLives(lives),
              TextUtils.pluralize("life", "lives", lives)));
    } else {
      OtherUtils.sendCommandFeedbackQuiet(source,
          TextUtils.format("{} has {} left", target, livesManager.getFormattedLives(lives)));
    }
    return 1;
  }

  public static int reloadLives(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "times";

    OtherUtils.sendCommandFeedback(source,
        TextUtils.formatLoosely("§7Reloading {}...", timeOrLives));
    currentSeason.reloadAllPlayerTeams();
    return 1;
  }

  public static int lifeManager(ServerCommandSource source, Collection<ServerPlayerEntity> targets,
      String timeArgument, boolean setNotGive, boolean reverse) {

    Integer amount = OtherUtils.parseTimeSecondsFromArgument(timeArgument);
    if (amount == null) {
      source.sendError(Text.literal(SessionCommand.INVALID_TIME_FORMAT_ERROR));
      return -1;
    }
      if (reverse) {
          amount *= -1;
      }

    return lifeManager(source, targets, amount, setNotGive);
  }

  public static int lifeManager(ServerCommandSource source, Collection<ServerPlayerEntity> targets,
      int amount, boolean setNotGive) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "time";

    if (setNotGive) {

      if (targets.size() == 1) {
        OtherUtils.sendCommandFeedback(source,
            TextUtils.format("Set {}'s {} to {}", targets.iterator().next(), timeOrLives,
                livesManager.getFormattedLives(amount)));
      } else {
        OtherUtils.sendCommandFeedback(source,
            TextUtils.format("Set {} to {} for {} targets", timeOrLives,
                livesManager.getFormattedLives(amount), targets.size()));
      }

      for (ServerPlayerEntity player : targets) {
        livesManager.setPlayerLives(player, amount);
      }
    } else {

      String addOrRemove = amount >= 0 ? "Added" : "Removed";
      String timeOrLives2 = Math.abs(amount) == 1 ? "life" : "lives";
      if (!normalLife) {
        timeOrLives2 = OtherUtils.formatTime(Math.abs(amount) * 20);
      }
      String toOrFrom = amount >= 0 ? "to" : "from";

      if (targets.size() == 1) {
        if (normalLife) {
          OtherUtils.sendCommandFeedback(source,
              TextUtils.format("{} {} {} {} {}", addOrRemove, Math.abs(amount), timeOrLives2,
                  toOrFrom, targets.iterator().next()));
        } else {
          OtherUtils.sendCommandFeedback(source,
              TextUtils.format("{} {} {} {}", addOrRemove, timeOrLives2, toOrFrom,
                  targets.iterator().next()));
        }
      } else {
        if (normalLife) {
          OtherUtils.sendCommandFeedback(source,
              TextUtils.format("{} {} {} {} {} targets", addOrRemove, Math.abs(amount),
                  timeOrLives2, toOrFrom, targets.size()));
        } else {
          OtherUtils.sendCommandFeedback(source,
              TextUtils.format("{} {} {} {} targets", addOrRemove, timeOrLives2, toOrFrom,
                  targets.size()));
        }
      }

      for (ServerPlayerEntity player : targets) {
        livesManager.addToPlayerLives(player, amount);
      }
    }
    if (currentSeason instanceof DoubleLife doubleLife) {
      targets.forEach(doubleLife::syncSoulboundLives);
    }
    return 1;
  }

  public static int resetLives(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
      if (checkBanned(source)) {
          return -1;
      }
      if (targets == null || targets.isEmpty()) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "time";

    targets.forEach(livesManager::resetPlayerLife);

    if (targets.size() == 1) {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Reset {}'s {}", targets.iterator().next(), timeOrLives));
    } else {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("Reset {} of {} targets", timeOrLives, targets.size()));
    }

    return 1;
  }

  public static int resetAllLives(ServerCommandSource source) {
      if (checkBanned(source)) {
          return -1;
      }
    boolean normalLife = isNormalLife();
    String timeOrLives = normalLife ? "lives" : "times";

    livesManager.resetAllPlayerLives();
    OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset everyone's {}", timeOrLives));
    return 1;
  }

  public static int assignRandomLives(ServerCommandSource source,
      Collection<ServerPlayerEntity> players) {
      if (checkBanned(source)) {
          return -1;
      }
      if (players == null || players.isEmpty()) {
          return -1;
      }

    if (players.size() == 1) {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("§7Assigning random lives to {}§7...", players.iterator().next()));
    } else {
      OtherUtils.sendCommandFeedback(source,
          TextUtils.format("§7Assigning random lives to {}§7 targets...", players.size()));
    }
    if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
      lastLifeLivesManager.assignRandomLives(players);
    }
    return 1;
  }
}
