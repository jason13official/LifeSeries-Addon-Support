package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.utils.AnimationUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.seriesConfig;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GivelifeCommand {

    public static boolean isAllowed() {
        if (currentSeries.getSeries() == SeriesList.LIMITED_LIFE) return false;
        return seriesConfig.GIVELIFE_COMMAND_ENABLED.get(seriesConfig);
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is not available."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("givelife")
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> giftLife(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                )
        );
    }

    public static int giftLife(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;

        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (target == null) return -1;
        if (!currentSeries.isAlive(self)) {
            source.sendError(Text.of("You do not have any lives to give."));
            return -1;
        }
        if (!currentSeries.isAlive(target)) {
            source.sendError(Text.of("That player is not alive."));
            return -1;
        }
        if (target == self) {
            source.sendError(Text.of("You cannot give a life to yourself."));
            return -1;
        }
        Integer currentLives = currentSeries.getPlayerLives(self);
        if (currentLives <= 1) {
            source.sendError(Text.of("You cannot give away your last life."));
            return -1;
        }
        Integer targetLives = currentSeries.getPlayerLives(target);
        if (targetLives >= currentSeries.GIVELIFE_MAX_LIVES) {
            source.sendError(Text.of("That player cannot receive any more lives."));
            return -1;
        }

        Text currentPlayerName = self.getStyledDisplayName();
        currentSeries.removePlayerLife(self);
        currentSeries.addToLifeNoUpdate(target);
        AnimationUtils.playTotemAnimation(self);
        TaskScheduler.scheduleTask(40, () -> currentSeries.receiveLifeFromOtherPlayer(currentPlayerName, target));

        return 1;
    }
}
