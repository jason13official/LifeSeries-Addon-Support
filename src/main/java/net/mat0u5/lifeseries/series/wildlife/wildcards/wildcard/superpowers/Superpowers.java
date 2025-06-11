package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum Superpowers {
    NONE,

    TIME_CONTROL,
    CREAKING,
    WIND_CHARGE,
    ASTRAL_PROJECTION,
    SUPER_PUNCH,
    MIMICRY,
    TELEPORTATION,
    LISTENING,
    SHADOW_PLAY,
    FLIGHT,
    PLAYER_DISGUISE,
    ANIMAL_DISGUISE,
    TRIPLE_JUMP,
    INVISIBILITY,
    SUPERSPEED,
    NECROMANCY;

    public static List<Superpowers> getImplemented() {
        List<Superpowers> result = new ArrayList<>(List.of(TIME_CONTROL, WIND_CHARGE, ASTRAL_PROJECTION, PLAYER_DISGUISE,
                ANIMAL_DISGUISE, SUPER_PUNCH, MIMICRY, TELEPORTATION, SHADOW_PLAY, INVISIBILITY,
                TRIPLE_JUMP, SUPERSPEED, NECROMANCY
                //? if >= 1.21.2 {
                , CREAKING, FLIGHT
                 //?}

        ));
        if (DependencyManager.voicechatLoaded()) result.add(LISTENING);
        return result;
    }

    @Nullable
    public static Superpower getInstance(ServerPlayerEntity player, Superpowers superpower) {
        if (superpower == TIME_CONTROL) return new TimeControl(player);
        if (superpower == WIND_CHARGE) return new WindCharge(player);
        if (superpower == ASTRAL_PROJECTION) return new AstralProjection(player);
        if (superpower == PLAYER_DISGUISE) return new PlayerDisguise(player);
        if (superpower == ANIMAL_DISGUISE) return new AnimalDisguise(player);
        if (superpower == SUPER_PUNCH) return new SuperPunch(player);
        if (superpower == MIMICRY) return new Mimicry(player);
        if (superpower == TELEPORTATION) return new Teleportation(player);
        if (superpower == SHADOW_PLAY) return new ShadowPlay(player);
        if (superpower == INVISIBILITY) return new Invisibility(player);
        if (superpower == TRIPLE_JUMP) return new TripleJump(player);
        if (superpower == SUPERSPEED) return new Superspeed(player);
        if (superpower == NECROMANCY) return new Necromancy(player);
        //? if >= 1.21.2 {
        if (superpower == CREAKING) return new Creaking(player);
        if (superpower == FLIGHT) return new Flight(player);
        //?}
        if (DependencyManager.voicechatLoaded()) {
            if (superpower == LISTENING) return new Listening(player);
        }
        return null;
    }

    public static List<String> getImplementedStr() {
        List<String> result = new ArrayList<>();
        for (Superpowers superpower : getImplemented()) {
            result.add(getString(superpower));
        }
        return result;
    }

    public static String getString(Superpowers superpower) {
        return superpower.toString().toLowerCase();
    }

    public static Superpowers fromString(String superpower) {
        try {
            return Enum.valueOf(Superpowers.class, superpower.toUpperCase());
        } catch(Exception e) {}
        return Superpowers.NONE;
    }
}
