package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class LivesCommand {

    public static boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.LIMITED_LIFE && currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is not available during Limited Life. Use '/limitedlife time' instead."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(
            literal("lives")
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
                        context.getSource(), EntityArgumentType.getPlayers(context, "player"), 1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                )
            )
            .then(literal("remove")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgumentType.getPlayers(context, "player"), -1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"), -IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                )
            )
            .then(literal("set")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.players())
                    .then(argument("amount", IntegerArgumentType.integer(0))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount"), true)
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
        );
    }

    public static int showLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;
        if (!currentSeason.hasAssignedLives(self)) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("You have not been assigned any lives yet"));
            return 1;
        }

        Integer playerLives = currentSeason.getPlayerLives(self);
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("You have {} {}", currentSeason.getFormattedLives(playerLives), TextUtils.pluralize("life", "lives", playerLives)));
        if (playerLives == null || playerLives <= 0) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Womp womp."));
        }

        return 1;
    }

    public static int getAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!ScoreboardUtils.existsObjective("Lives")) {
            source.sendError(Text.of("Nobody has been assigned lives yet"));
            return -1;
        }

        Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores("Lives");
        if (entries.isEmpty()) {
            source.sendError(Text.of("Nobody has been assigned lives yet"));
            return -1;
        }
        MutableText text = Text.literal("Assigned Lives: \n");
        for (ScoreboardEntry entry : entries) {
            String name = entry.owner();
            if (name.startsWith("`")) continue;
            int lives = entry.value();
            Formatting color = currentSeason.getColorForLives(lives);
            text.append(TextUtils.format("{} has {} {}\n", Text.literal(name).formatted(color), currentSeason.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
        }

        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public static int getLivesFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (!currentSeason.hasAssignedLives(target)) {
            source.sendError(TextUtils.formatPlain("{} has not been assigned any lives", target));
            return -1;
        }
        Integer lives = currentSeason.getPlayerLives(target);
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{} has {} {}", target, currentSeason.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
        return 1;
    }

    public static int reloadLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Text.of("§7Reloading lives..."));
        currentSeason.reloadAllPlayerTeams();
        return 1;
    }

    public static int lifeManager(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (setNotGive) {

            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s lives to {}", targets.iterator().next(), currentSeason.getFormattedLives(amount)));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set lives to {} for {} targets", currentSeason.getFormattedLives(amount), targets.size()));
            }

            for (ServerPlayerEntity player : targets) {
                currentSeason.setPlayerLives(player, amount);
            }
        }
        else {

            String addOrRemove = amount >= 0 ? "Added" : "Removed";
            String lifeOrLives = Math.abs(amount)==1?"life":"lives";
            String toOrFrom = amount >= 0 ? "to" : "from";

            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} {}", addOrRemove, Math.abs(amount), lifeOrLives, toOrFrom, targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} {} targets", addOrRemove, Math.abs(amount), lifeOrLives, toOrFrom, targets.size()));
            }

            for (ServerPlayerEntity player : targets) {
                currentSeason.addToPlayerLives(player,amount);
            }
        }
        if (currentSeason instanceof DoubleLife doubleLife) {
            targets.forEach(doubleLife::syncSoulboundLives);
        }
        return 1;
    }

    public static int resetLives(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        targets.forEach(currentSeason::resetPlayerLife);

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s lives", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset lives of {} targets", targets.size()));
        }

        return 1;
    }

    public static int resetAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        currentSeason.resetAllPlayerLives();
        OtherUtils.sendCommandFeedback(source, Text.literal("Reset everyone's lives"));
        return 1;
    }
}
