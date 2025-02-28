package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.Main.seriesConfig;

public class AttributeUtils {
    public static void resetAttributesOnPlayerJoin(ServerPlayerEntity player) {

        if (currentSeries.getSeries() != SeriesList.SECRET_LIFE && !(player.getMaxHealth() == 13 && TriviaBot.cursedHeartPlayers.contains(player.getUuid()))) {
            resetMaxPlayerHealth(player);
        }
        if (!TriviaBot.cursedMoonJumpPlayers.contains(player.getUuid())) {
            resetPlayerJumpHeight(player);
        }
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP) &&
            !SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
            resetSafeFallHeight(player);
        }
    }

    public static void resetMaxPlayerHealth(ServerPlayerEntity player) {
        double health = seriesConfig.getOrCreateDouble("max_player_health", 20.0d);
        setMaxPlayerHealth(player, health);
    }

    public static void resetPlayerJumpHeight(ServerPlayerEntity player) {
        setPlayerJumpHeight(player, 0.41999998688697815);
    }

    public static void resetSafeFallHeight(ServerPlayerEntity player) {
        setSafeFallHeight(player, 3);
    }

    /*
        Setters
     */

    public static void setMaxPlayerHealth(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(value);*/
    }

    public static void setPlayerJumpHeight(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);*/
    }

    public static void setSafeFallHeight(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE)).setBaseValue(value);*/
    }

    public static void setScale(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(value);
        //?} else {
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SCALE)).setBaseValue(value);
         *///?}
    }

    public static void setJumpStrength(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);*/
    }

    /*
        Getters
     */
    public static double getMaxPlayerHealth(ServerPlayerEntity player) {
        //? if <=1.21 {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
        //?} else
        /*return player.getAttributeBaseValue(EntityAttributes.MAX_HEALTH);*/
    }
}
