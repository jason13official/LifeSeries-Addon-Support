package net.mat0u5.lifeseries.seasons.season.limitedlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.SessionCommand;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
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
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("30m", "1h"), builder))
                                        .executes(context -> lifeManager(
                                                context.getSource(), EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "time"), false, false)
                                        )
                                )
                        )
                )
                .then(literal("remove")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("30m", "1h"), builder))
                                        .executes(context -> lifeManager(
                                                context.getSource(), EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "time"), false, true)
                                        )
                                )
                        )
                )
                .then(literal("set")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("time", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(List.of("8h", "16h", "24h"), builder))
                                        .executes(context -> lifeManager(
                                                context.getSource(), EntityArgumentType.getPlayer(context, "player"),
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
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> resetLives(
                                        context.getSource(), EntityArgumentType.getPlayer(context, "player"))
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

        final ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;
        if (!currentSeason.hasAssignedLives(self)) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("You have not been assigned any lives yet."));
            return 1;
        }

        Integer playerLives = currentSeason.getPlayerLives(self);
        OtherUtils.sendCommandFeedbackQuiet(source, Text.literal("You have ").append(currentSeason.getFormattedLives(playerLives)).append(Text.of(" left.")));
        if (playerLives == null || playerLives <= 0) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Womp womp."));
        }

        return 1;
    }

    public static int getAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!ScoreboardUtils.existsObjective("Lives")) {
            source.sendError(Text.of("Nobody has been assigned time yet."));
            return -1;
        }

        Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores("Lives");
        if (entries.isEmpty()) {
            source.sendError(Text.of("Nobody has been assigned time yet."));
            return -1;
        }
        MutableText text = Text.literal("Assigned Time: \n");
        for (ScoreboardEntry entry : entries) {
            String name = entry.owner();
            if (name.startsWith("`")) continue;
            int lives = entry.value();
            Formatting color = currentSeason.getColorForLives(lives);

            MutableText pt1 = Text.literal("").append(Text.literal(name).formatted(color)).append(Text.literal(" has "));
            Text pt2 = currentSeason.getFormattedLives(lives);
            Text pt3 = Text.of("\n");
            text.append(pt1.append(pt2).append(pt3));
        }

        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public static int getLivesFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (!currentSeason.hasAssignedLives(target)) {
            source.sendError(Text.of(target.getNameForScoreboard()+" has not been assigned any lives."));
            return -1;
        }
        Integer lives = currentSeason.getPlayerLives(target);
        MutableText pt1 = Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" has "));
        Text pt2 = currentSeason.getFormattedLives(lives);
        Text pt3 = Text.of(" left.");

        OtherUtils.sendCommandFeedbackQuiet(source, pt1.append(pt2).append(pt3));
        return 1;
    }

    public static int reloadLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Text.of("Reloading lives..."));
        currentSeason.reloadAllPlayerTeams();
        return 1;
    }

    public static int lifeManager(ServerCommandSource source, ServerPlayerEntity target, String timeArgument, boolean setNotGive, boolean reverse) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        Integer amount = OtherUtils.parseTimeSecondsFromArgument(timeArgument);
        if (amount == null) {
            source.sendError(Text.literal(SessionCommand.INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        if (reverse) amount *= -1;

        if (setNotGive) {
            currentSeason.setPlayerLives(target,amount);
            Text finalText = Text.literal("Set ").append(target.getStyledDisplayName()).append(Text.of("'s time to ")).append(currentSeason.getFormattedLives(target));
            OtherUtils.sendCommandFeedback(source, finalText);
        }
        else {
            currentSeason.addToPlayerLives(target,amount);
            String pt1 = amount >= 0 ? "Added" : "Removed";
            String pt2 = " "+OtherUtils.formatTime(Math.abs(amount)*20);
            String pt4 = amount >= 0 ? " to " : " from ";
            Text finalText = Text.of(pt1+pt2+pt4).copy().append(target.getStyledDisplayName()).append(".");
            OtherUtils.sendCommandFeedback(source, finalText);
        }

        return 1;
    }

    public static int resetLives(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        currentSeason.resetPlayerLife(target);
        OtherUtils.sendCommandFeedback(source, Text.literal("Reset ").append(target.getStyledDisplayName()).append(Text.of("'s lives.")));
        return 1;
    }

    public static int resetAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        currentSeason.resetAllPlayerLives();
        OtherUtils.sendCommandFeedback(source, Text.literal("Reset everyone's lives."));
        return 1;
    }
}
