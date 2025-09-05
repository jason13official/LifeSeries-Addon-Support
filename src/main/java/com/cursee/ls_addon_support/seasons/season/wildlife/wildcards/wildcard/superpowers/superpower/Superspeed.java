package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.AttributeUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class Superspeed extends ToggleableSuperpower {

  public static boolean STEP_UP = false;

  public Superspeed(ServerPlayerEntity player) {
    super(player);
  }

  public static void slowlySetSpeed(ServerPlayerEntity player, double speed, int ticks) {
      if (server == null) {
          return;
      }
    double currentSpeed = AttributeUtils.getMovementSpeed(player);
    double step = (speed - currentSpeed) / ticks;
    for (int i = 0; i < ticks; i++) {
      int finalI = i;
      TaskScheduler.scheduleTask(i,
          () -> AttributeUtils.setMovementSpeed(player, currentSpeed + (step * finalI)));
    }
    TaskScheduler.scheduleTask(ticks + 1, () -> AttributeUtils.setMovementSpeed(player, speed));
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.SUPERSPEED;
  }

  @Override
  public void tick() {
      if (!active) {
          return;
      }
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    StatusEffectInstance hunger = new StatusEffectInstance(StatusEffects.HUNGER, 219, 4, false,
        false, false);
    player.addStatusEffect(hunger);
    player.getHungerManager().setSaturationLevel(0);
    if (player.getHungerManager().getFoodLevel() <= 6) {
      deactivate();
    }
  }

  @Override
  public void activate() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    if (player.getHungerManager().getFoodLevel() <= 6) {
        player.playSoundToPlayer(SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.MASTER, 1, 1);
      return;
    }
    player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 1, 1);
    slowlySetSpeed(player, 0.35, 60);
    NetworkHandlerServer.sendVignette(player, -1);
    if (STEP_UP) {
      AttributeUtils.setStepHeight(player, 1);
    }
    super.activate();
  }

  @Override
  public int activateCooldownMillis() {
    return 3050;
  }

  @Override
  public void deactivate() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 1, 1);
    slowlySetSpeed(player, AttributeUtils.DEFAULT_PLAYER_MOVEMENT_SPEED, 30);
    if (!WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
      player.removeStatusEffect(StatusEffects.HUNGER);
      StatusEffectInstance hunger = new StatusEffectInstance(StatusEffects.HUNGER, 30, 4, false,
          false, false);
      player.addStatusEffect(hunger);
    }
    NetworkHandlerServer.sendVignette(player, 0);
    AttributeUtils.resetStepHeight(player);
    super.deactivate();
  }
}
