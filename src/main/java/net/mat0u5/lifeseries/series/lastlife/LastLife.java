package net.mat0u5.lifeseries.series.lastlife;

import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.series.*;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PermissionManager;
import net.mat0u5.lifeseries.utils.ScoreboardUtils;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

import static net.mat0u5.lifeseries.Main.seriesConfig;

public class LastLife extends Series {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /givelife, /boogeyman, /lastlife";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /givelife";
    public static int ROLL_MAX_LIVES = 6;
    public static int ROLL_MIN_LIVES = 2;

    public LastLifeLivesManager livesManager = new LastLifeLivesManager();
    public BoogeymanManager boogeymanManager = new BoogeymanManager();

    @Override
    public SeriesList getSeries() {
        return SeriesList.LAST_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new LastLifeConfig();
    }

    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            boogeymanManager.resetBoogeymen();
            activeActions.addAll(List.of(
                    livesManager.actionChooseLives,
                    boogeymanManager.actionBoogeymanWarn1,
                    boogeymanManager.actionBoogeymanWarn2,
                    boogeymanManager.actionBoogeymanChoose
            ));
            return true;
        }
        return false;
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        boogeymanManager.sessionEnd();
    }

    @Override
    public void playerLostAllLives(ServerPlayerEntity player, Integer livesBefore) {
        super.playerLostAllLives(player, livesBefore);
        boogeymanManager.playerLostAllLives(player);
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        Boogeyman boogeyman  = boogeymanManager.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured) {
            if (isAllowedToAttack(killer, victim)) return;
            OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"+killer.getNameForScoreboard() +
                    "§7, who is not §cred name§7, nor a §cboogeyman§7!"));
            return;
        }
        if (isOnLastLife(victim, true)) {
            return;
        }
        boogeymanManager.cure(killer);
    }

    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        super.onClaimKill(killer, victim);
        Boogeyman boogeyman  = boogeymanManager.getBoogeyman(killer);
        if (boogeyman == null || boogeyman.cured) return;
        if (isOnLastLife(victim, true)) {
            return;
        }
        boogeymanManager.cure(killer);
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (isOnLastLife(attacker, false)) return true;
        if (attacker.getPrimeAdversary() == victim && isOnLastLife(victim, false)) return true;
        Boogeyman boogeymanAttacker = boogeymanManager.getBoogeyman(attacker);
        Boogeyman boogeymanVictim = boogeymanManager.getBoogeyman(victim);
        if (boogeymanAttacker != null && !boogeymanAttacker.cured) return true;
        return attacker.getPrimeAdversary() == victim && (boogeymanVictim != null && !boogeymanVictim.cured);
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);
        boogeymanManager.onPlayerJoin(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Last Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Last Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seriesConfig instanceof LastLifeConfig config)) return;
        ROLL_MIN_LIVES = config.RANDOM_LIVES_MIN.get(config);
        ROLL_MAX_LIVES = config.RANDOM_LIVES_MAX.get(config);
    }
}
