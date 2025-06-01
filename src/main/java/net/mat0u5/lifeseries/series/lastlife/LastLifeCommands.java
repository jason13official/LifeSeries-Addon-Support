package net.mat0u5.lifeseries.series.lastlife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LastLifeCommands {


    public static boolean isAllowed() {
        return currentSeries.getSeries() == SeriesList.LAST_LIFE;
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

        ((LastLife) currentSeries).livesManager.assignRandomLives(players);
        return 1;
    }
}
