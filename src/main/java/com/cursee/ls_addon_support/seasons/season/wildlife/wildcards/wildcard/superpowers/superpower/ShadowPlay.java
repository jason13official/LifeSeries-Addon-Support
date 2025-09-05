package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ShadowPlay extends Superpower {

  public ShadowPlay(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.SHADOW_PLAY;
  }

  @Override
  public int getCooldownMillis() {
    return 30000;
  }

  @Override
  public void activate() {
    super.activate();
    ServerPlayerEntity player = getPlayer();
    if (player == null) {
      return;
    }
    ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
    List<ServerPlayerEntity> affectedPlayers = playerWorld.getEntitiesByClass(
        ServerPlayerEntity.class, player.getBoundingBox().expand(10),
        playerEntity -> playerEntity.distanceTo(player) <= 10);
    StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0);
    StatusEffectInstance invis = new StatusEffectInstance(StatusEffects.INVISIBILITY, 60, 0, false,
        false, false);
    affectedPlayers.remove(player);
    for (ServerPlayerEntity affectedPlayer : affectedPlayers) {
      affectedPlayer.addStatusEffect(blindness);
      PlayerUtils.getServerWorld(affectedPlayer).spawnParticles(
          ParticleTypes.SMOKE,
          affectedPlayer.getX(), affectedPlayer.getY() + 0.9, affectedPlayer.getZ(),
          40, 0.3, 0.5, 0.3, 0
      );
    }
    player.addStatusEffect(invis);
    playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.MASTER, 1, 1);
    NetworkHandlerServer.sendPlayerInvisible(player.getUuid(), System.currentTimeMillis() + 3000);
  }
}
