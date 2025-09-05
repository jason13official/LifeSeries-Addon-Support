package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class Invisibility extends ToggleableSuperpower {

  public Invisibility(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.INVISIBILITY;
  }

  @Override
  public int deactivateCooldownMillis() {
    return 5000;
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
    StatusEffectInstance invis = new StatusEffectInstance(StatusEffects.INVISIBILITY, 219, 0, false,
        false, false);
    player.addStatusEffect(invis);
  }

  @Override
  public void activate() {
    super.activate();
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    ServerWorld playerWorld = PlayerUtils.getServerWorld(player);

    playerWorld.spawnParticles(
        ParticleTypes.SMOKE,
        player.getX(), player.getY() + 0.9, player.getZ(),
        40, 0.3, 0.5, 0.3, 0
    );

    playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.MASTER, 1, 1);
    NetworkHandlerServer.sendPlayerInvisible(player.getUuid(), -1);
  }

  @Override
  public void deactivate() {
    super.deactivate();
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    PlayerUtils.getServerWorld(player).playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 1, 1);
    NetworkHandlerServer.sendPlayerInvisible(player.getUuid(), 0);
    player.removeStatusEffect(StatusEffects.INVISIBILITY);
  }

  public void onTakeDamage() {
    deactivate();
  }

  public void onAttack() {
    deactivate();
  }
}
