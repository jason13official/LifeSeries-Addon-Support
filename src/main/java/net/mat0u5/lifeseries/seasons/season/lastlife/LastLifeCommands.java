package net.mat0u5.lifeseries.seasons.season.lastlife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.utils.player.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LastLifeCommands {


    public static boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.LAST_LIFE;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is only available when playing Last Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("lastlife")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(literal("rollLives")
                    .executes(context -> LastLifeCommands.assignRandomLives(
                        context.getSource(), PlayerUtils.getAllPlayers()
                    ))
                    .then(argument("players", EntityArgumentType.players())
                        .executes(context -> LastLifeCommands.assignRandomLives(
                            context.getSource(), EntityArgumentType.getPlayers(context, "players")
                        ))
                    )
                )
        );
    }

    public static int assignRandomLives(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        if (checkBanned(source)) return -1;
        if (players == null || players.isEmpty()) return -1;

        if (players.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Assigning random lives to {}§7...", players.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Assigning random lives to {}§7 targets...", players.size()));
        }
        ((LastLife) currentSeason).livesManager.assignRandomLives(players);
        return 1;
    }
}
