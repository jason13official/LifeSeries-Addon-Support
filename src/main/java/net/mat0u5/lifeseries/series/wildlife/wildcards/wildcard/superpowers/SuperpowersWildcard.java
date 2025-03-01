package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeries;

public class SuperpowersWildcard extends Wildcard {
    private static final Map<UUID, Superpower> playerSuperpowers = new HashMap<>();

    @Override
    public Wildcards getType() {
        return Wildcards.SUPERPOWERS;
    }

    @Override
    public void activate() {
        rollRandomSuperpowers();
        super.activate();
    }

    @Override
    public void deactivate() {
        resetAllSuperpowers();
        super.deactivate();
    }

    public static void onTick() {
        playerSuperpowers.values().forEach(Superpower::tick);
    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(Superpower::turnOff);
        playerSuperpowers.clear();
    }

    public static void rollRandomSuperpowers() {
        resetAllSuperpowers();
        List<Superpowers> implemented = new java.util.ArrayList<>(Superpowers.getImplemented());
        if (implemented.contains(Superpowers.NECROMANCY) && !Necromancy.shouldBeIncluded()) {
            implemented.remove(Superpowers.NECROMANCY);
        }

        Collections.shuffle(implemented);
        int pos = 0;
        List<ServerPlayerEntity> allPlayers = currentSeries.getAlivePlayers();
        Collections.shuffle(allPlayers);
        for (ServerPlayerEntity player : allPlayers) {
            Superpowers power = implemented.get(pos%implemented.size());
            Superpower instance = Superpowers.getInstance(player, power);
            if (instance != null) playerSuperpowers.put(player.getUuid(), instance);
            pos++;
        }
        PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.of(Identifier.of("minecraft","wildlife_superpowers")), 0.2f, 1);
    }

    public static void setSuperpower(ServerPlayerEntity player, Superpowers superpower) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            playerSuperpowers.get(player.getUuid()).turnOff();
        }
        Superpower instance = Superpowers.getInstance(player, superpower);
        if (instance != null) playerSuperpowers.put(player.getUuid(), instance);
        PlayerUtils.playSoundToPlayers(List.of(player), SoundEvent.of(Identifier.of("minecraft","wildlife_superpowers")), 0.2f, 1);
    }

    public static void pressedSuperpowerKey(ServerPlayerEntity player) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            playerSuperpowers.get(player.getUuid()).onKeyPressed();
        }
    }

    public static boolean hasActivePower(ServerPlayerEntity player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return false;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().getSuperpower() == superpower;
        }
        return power.getSuperpower() == superpower;
    }

    public static boolean hasActivatedPower(ServerPlayerEntity player, Superpowers superpower) {
        if (!hasActivePower(player, superpower)) return false;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
            return mimicry.getMimickedPower().active;
        }
        return power.active;
    }

    public static Superpowers getSuperpower(ServerPlayerEntity player) {
        if (playerSuperpowers.containsKey(player.getUuid())) {
            Superpower power = playerSuperpowers.get(player.getUuid());
            if (power instanceof Mimicry mimicry) {
                return mimicry.getMimickedPower().getSuperpower();
            }
            return power.getSuperpower();
        }
        return Superpowers.NONE;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayerEntity player) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return null;
        Superpower power = playerSuperpowers.get(player.getUuid());
        if (power instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower();
        }
        return power;
    }
}
