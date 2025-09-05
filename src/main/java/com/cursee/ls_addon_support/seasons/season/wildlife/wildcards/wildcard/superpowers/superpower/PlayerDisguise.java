package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class PlayerDisguise extends ToggleableSuperpower {

  private String copiedPlayerName = "";
  private String copiedPlayerUUID = "";

  public PlayerDisguise(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.PLAYER_DISGUISE;
  }

  @Override
  public int deactivateCooldownMillis() {
    return 10000;
  }

  @Override
  public void activate() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
    if (lookingAt != null) {
      if (lookingAt instanceof ServerPlayerEntity lookingAtPlayer) {
        lookingAtPlayer = PlayerUtils.getPlayerOrProjection(lookingAtPlayer);
        if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
          copiedPlayerUUID = lookingAtPlayer.getUuidAsString();
          copiedPlayerName = TextUtils.textToLegacyString(lookingAtPlayer.getStyledDisplayName());
          player.playSoundToPlayer(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER,
              0.3f, 1);
          PlayerUtils.displayMessageToPlayer(player,
              TextUtils.format("Copied DNA of {}", lookingAtPlayer)
                  .append(Text.of(" — Press again to disguise")), 65);
          return;
        }
      }
    }

    if (copiedPlayerName.isEmpty() || copiedPlayerUUID.isEmpty()) {
      PlayerUtils.displayMessageToPlayer(player, Text.of("You are not looking at a player."), 65);
      return;
    }

    ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
    playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, SoundCategory.MASTER, 1, 1);
    playerWorld.spawnParticles(
        ParticleTypes.EXPLOSION,
        player.getPos().getX(), player.getPos().getY(), player.getPos().getZ(),
        2, 0, 0, 0, 0
    );
    NetworkHandlerServer.sendPlayerDisguise(player.getUuid().toString(),
        player.getName().getString(), copiedPlayerUUID, copiedPlayerName);

    super.activate();
  }

  @Override
  public void deactivate() {
    super.deactivate();
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
    playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.MASTER, 1, 1);
    playerWorld.spawnParticles(
        ParticleTypes.EXPLOSION,
        player.getPos().getX(), player.getPos().getY(), player.getPos().getZ(),
        2, 0, 0, 0, 0
    );
    NetworkHandlerServer.sendPlayerDisguise(player.getUuid().toString(),
        player.getName().getString(), "", "");
  }

  public void onTakeDamage() {
    deactivate();
  }
}
