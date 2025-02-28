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
    public static final double DEFAULT_PLAYER_MAX_HEALTH = 20.0;
    public static final double DEFAULT_PLAYER_JUMP_HEIGHT = 0.41999998688697815;
    public static final double DEFAULT_PLAYER_SAFE_FALL_HEIGHT = 3.0;
    public static final double DEFAULT_PLAYER_MOVEMENT_SPEED = 0.10000000149011612;


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
        resetMovementSpeed(player);
    }

    public static void resetMaxPlayerHealth(ServerPlayerEntity player) {
        double health = seriesConfig.getOrCreateDouble("max_player_health", DEFAULT_PLAYER_MAX_HEALTH);
        setMaxPlayerHealth(player, health);
    }

    public static void resetPlayerJumpHeight(ServerPlayerEntity player) {
        setPlayerJumpHeight(player, DEFAULT_PLAYER_JUMP_HEIGHT);
    }

    public static void resetSafeFallHeight(ServerPlayerEntity player) {
        setSafeFallHeight(player, DEFAULT_PLAYER_SAFE_FALL_HEIGHT);
    }

    public static void resetMovementSpeed(ServerPlayerEntity player) {
        setMovementSpeed(player, DEFAULT_PLAYER_MOVEMENT_SPEED);
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
    
    public static void setMovementSpeed(ServerPlayerEntity player, double value) {
        if (player == null) return;
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(value);*/
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

    public static double getMovementSpeed(ServerPlayerEntity player) {
        //? if <=1.21 {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        //?} else
        /*return player.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED);*/
    }
}
