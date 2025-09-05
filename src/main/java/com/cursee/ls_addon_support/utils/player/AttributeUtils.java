package com.cursee.ls_addon_support.utils.player;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;

import com.cursee.ls_addon_support.dependencies.DependencyManager;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import java.util.Objects;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class AttributeUtils {

  public static final double DEFAULT_PLAYER_JUMP_HEIGHT = 0.41999998688697815;
  public static final double DEFAULT_PLAYER_SAFE_FALL_HEIGHT = 3.0;
  public static final double DEFAULT_PLAYER_MOVEMENT_SPEED = 0.10000000149011612;
  public static final double DEFAULT_PLAYER_STEP_HEIGHT = 0.6;


  public static void resetAttributesOnPlayerJoin(ServerPlayerEntity player) {
    resetMaxPlayerHealthIfNecessary(player);
    if (DependencyManager.wildLifeModsLoaded()) {
      if (!TriviaBot.cursedMoonJumpPlayers.contains(player.getUuid())) {
        resetPlayerJumpHeight(player);
      }
    }
    if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
      resetSafeFallHeight(player);
    }
    resetMovementSpeed(player);
    resetStepHeight(player);
  }

  public static void resetMaxPlayerHealthIfNecessary(ServerPlayerEntity player) {
      if (currentSeason.getSeason() == Seasons.SECRET_LIFE) {
          return;
      }
    double currentMaxHealth = getMaxPlayerHealth(player);
      if (DependencyManager.wildLifeModsLoaded() && currentMaxHealth == 13
          && TriviaBot.cursedHeartPlayers.contains(player.getUuid())) {
          return;
      }
      if (currentMaxHealth == 8 && Necromancy.isRessurectedPlayer(player)) {
          return;
      }
    resetMaxPlayerHealth(player);
  }

  public static void resetMaxPlayerHealth(ServerPlayerEntity player) {
    double health = seasonConfig.MAX_PLAYER_HEALTH.get(seasonConfig);
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

  public static void resetStepHeight(ServerPlayerEntity player) {
    setStepHeight(player, DEFAULT_PLAYER_STEP_HEIGHT);
  }

    /*
        Setters
     */

  public static void setMaxPlayerHealth(ServerPlayerEntity player, double value) {
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(value);
  }

  public static void setPlayerJumpHeight(ServerPlayerEntity player, double value) {
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);
  }

  public static void setSafeFallHeight(ServerPlayerEntity player, double value) {
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE)).setBaseValue(value);
  }

  public static void setScale(ServerPlayerEntity player, double value) {
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SCALE)).setBaseValue(value);
  }

  public static void setJumpStrength(ServerPlayerEntity player, double value) {
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);
  }

  public static void setMovementSpeed(ServerPlayerEntity player, double value) {
      if (player == null) {
          return;
      }
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(value);
  }

  public static void setStepHeight(ServerPlayerEntity player, double value) {
      if (player == null) {
          return;
      }
    Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.STEP_HEIGHT)).setBaseValue(value);
  }

  /*
      Getters
   */
  public static double getMaxPlayerHealth(ServerPlayerEntity player) {
    return player.getAttributeBaseValue(EntityAttributes.MAX_HEALTH);
  }

  public static double getMovementSpeed(ServerPlayerEntity player) {
    return player.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED);
  }

  public static double getPlayerSize(ServerPlayerEntity player) {
    return player.getAttributeBaseValue(EntityAttributes.SCALE);
  }
}
