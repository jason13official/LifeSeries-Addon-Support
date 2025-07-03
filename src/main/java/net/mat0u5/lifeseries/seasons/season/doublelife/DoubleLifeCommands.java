package net.mat0u5.lifeseries.seasons.season.doublelife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DoubleLifeCommands {

    public static boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.DOUBLE_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Double Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("soulmate")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(literal("get")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getSoulmate(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("set")
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("soulmate", EntityArgumentType.player())
                            .executes(context -> setSoulmate(
                                context.getSource(),
                                EntityArgumentType.getPlayer(context, "player"),
                                EntityArgumentType.getPlayer(context, "soulmate")
                            ))
                        )
                    )
                )
                .then(literal("list")
                    .executes(context -> listSoulmates(context.getSource()))
                )
                .then(literal("reset")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> resetSoulmate(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("resetAll")
                    .executes(context -> resetAllSoulmates(context.getSource()))
                )
                .then(literal("rollRandom")
                    .executes(context -> rollSoulmates(context.getSource()))
                )
        );
    }

    public static int setSoulmate(ServerCommandSource source, ServerPlayerEntity player, ServerPlayerEntity soulmate) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (season.hasSoulmate(player)) {
            source.sendError(Text.of(player.getNameForScoreboard()+" already has a soulmate."));
            return -1;
        }
        if (season.hasSoulmate(soulmate)) {
            source.sendError(Text.of(soulmate.getNameForScoreboard()+" already has a soulmate."));
            return -1;
        }

        season.setSoulmate(player,soulmate);
        season.saveSoulmates();

        OtherUtils.sendCommandFeedback(source, Text.literal("").append(player.getStyledDisplayName()).append(Text.of("'s soulmate is now ")).append(soulmate.getStyledDisplayName()));
        return 1;
    }

    public static int getSoulmate(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (!season.hasSoulmate(player)) {
            source.sendError(Text.of(player.getNameForScoreboard()+" does not have a soulmate."));
            return -1;
        }
        if (!season.isSoulmateOnline(player)) {
            source.sendError(Text.of(player.getNameForScoreboard()+" 's soulmate is not online right now."));
            return -1;
        }

        ServerPlayerEntity soulmate = season.getSoulmate(player);
        if (soulmate == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.literal("").append(player.getStyledDisplayName()).append(Text.of("'s soulmate is ")).append(soulmate.getStyledDisplayName()).append("."));
        return 1;
    }

    public static int resetSoulmate(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (!season.hasSoulmate(player)) {
            source.sendError(Text.of(player.getNameForScoreboard()+" does not have a soulmate."));
            return -1;
        }

        season.resetSoulmate(player);
        season.saveSoulmates();

        OtherUtils.sendCommandFeedback(source, Text.literal("").append(player.getStyledDisplayName()).append(Text.of("'s soulmate was reset.")));
        return 1;
    }

    public static int resetAllSoulmates(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        season.resetAllSoulmates();

        OtherUtils.sendCommandFeedback(source, Text.of("All soulmate entries were reset."));
        return 1;
    }

    public static int listSoulmates(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        boolean noSoulmates = true;
        for (Map.Entry<UUID, UUID> entry : season.soulmatesOrdered.entrySet()) {
            noSoulmates = false;
            ServerPlayerEntity player = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayerEntity soulmate = PlayerUtils.getPlayer(entry.getValue());
            MutableText pt1 = Text.literal(entry.getKey().toString());
            Text pt2 = Text.of("'s soulmate is ");
            Text pt3 = Text.of(entry.getValue().toString());
            if (player != null) {
                pt1 = Text.literal("").append(player.getStyledDisplayName());
            }
            if (soulmate != null) {
                pt3 = soulmate.getStyledDisplayName();
            }
            OtherUtils.sendCommandFeedbackQuiet(source, pt1.append(pt2).append(pt3));
        }

        if (noSoulmates) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("There are no soulmates currently assigned."));
        }
        return 1;
    }

    public static int rollSoulmates(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        DoubleLife season = ((DoubleLife) currentSeason);
        OtherUtils.sendCommandFeedback(source, Text.of("Rolling soulmates..."));
        season.rollSoulmates();
        return 1;
    }
}
