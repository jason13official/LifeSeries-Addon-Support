package net.mat0u5.lifeseries.seasons.season.limitedlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.SessionCommand;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LimitedLifeCommands {

    public static boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.LIMITED_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Limited Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("limitedlife")
            .requires(source -> isAllowed())
            .then(literal("time")
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
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("30m", "1h"), builder))
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
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("30m", "1h"), builder))
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
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("8h", "16h", "24h"), builder))
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
            )
        );
    }

    public static int showLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;
        if (!currentSeason.hasAssignedLives(self)) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("You have not been assigned any time yet"));
            return 1;
        }

        Integer playerLives = currentSeason.getPlayerLives(self);
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("You have {} left", currentSeason.getFormattedLives(playerLives)));
        if (playerLives == null || playerLives <= 0) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Womp womp."));
        }

        return 1;
    }

    public static int getAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!ScoreboardUtils.existsObjective("Lives")) {
            source.sendError(Text.of("Nobody has been assigned time yet"));
            return -1;
        }

        Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores("Lives");
        if (entries.isEmpty()) {
            source.sendError(Text.of("Nobody has been assigned time yet"));
            return -1;
        }
        MutableText text = Text.literal("Assigned Time: \n");
        for (ScoreboardEntry entry : entries) {
            String name = entry.owner();
            if (name.startsWith("`")) continue;
            int lives = entry.value();
            Formatting color = currentSeason.getColorForLives(lives);
            text.append(TextUtils.format("{} has {} left\n", Text.literal(name).formatted(color), currentSeason.getFormattedLives(lives)));
        }

        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public static int getLivesFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (!currentSeason.hasAssignedLives(target)) {
            source.sendError(TextUtils.formatPlain("{} has not been assigned any time", target));
            return -1;
        }
        Integer lives = currentSeason.getPlayerLives(target);
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{} has {} left", target,currentSeason.getFormattedLives(lives)));
        return 1;
    }

    public static int reloadLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Text.of("§7Reloading times..."));
        currentSeason.reloadAllPlayerTeams();
        return 1;
    }

    public static int lifeManager(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String timeArgument, boolean setNotGive, boolean reverse) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        Integer amount = OtherUtils.parseTimeSecondsFromArgument(timeArgument);
        if (amount == null) {
            source.sendError(Text.literal(SessionCommand.INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        if (reverse) amount *= -1;

        if (setNotGive) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s time to {}", targets.iterator().next(), currentSeason.getFormattedLives(amount)));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set time to {} for {} targets", currentSeason.getFormattedLives(amount), targets.size()));
            }

            for (ServerPlayerEntity player : targets) {
                currentSeason.setPlayerLives(player, amount);
            }
        }
        else {
            String addOrRemove = amount >= 0 ? "Added" : "Removed";
            String time = OtherUtils.formatTime(Math.abs(amount)*20);
            String toOrFrom = amount >= 0 ? "to" : "from";

            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {}", addOrRemove, time, toOrFrom, targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} targets", addOrRemove, time, toOrFrom, targets.size()));
            }

            for (ServerPlayerEntity player : targets) {
                currentSeason.addToPlayerLives(player,amount);
            }
        }

        return 1;
    }

    public static int resetLives(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayerEntity player : targets) {
            currentSeason.resetPlayerLife(player);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s time", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset time of {} targets", targets.size()));
        }
        return 1;
    }

    public static int resetAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        currentSeason.resetAllPlayerLives();
        OtherUtils.sendCommandFeedback(source, Text.literal("Reset everyone's time"));
        return 1;
    }
}
