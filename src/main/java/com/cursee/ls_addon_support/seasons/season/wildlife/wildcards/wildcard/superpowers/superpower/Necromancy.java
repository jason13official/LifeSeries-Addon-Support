package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;

import com.cursee.ls_addon_support.seasons.season.wildlife.WildLifeConfig;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.AttributeUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.world.WorldUitls;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class Necromancy extends Superpower {

  private static final List<UUID> ressurectedPlayers = new ArrayList<>();
  private static final List<UUID> clearedPlayers = new ArrayList<>();
  private final List<UUID> perPlayerRessurections = new ArrayList<>();

  public Necromancy(ServerPlayerEntity player) {
    super(player);
  }

  public static List<ServerPlayerEntity> getDeadSpectatorPlayers() {
    List<ServerPlayerEntity> deadPlayers = new ArrayList<>();
    for (ServerPlayerEntity player : livesManager.getDeadPlayers()) {
        if (!player.isSpectator()) {
            continue;
        }
      deadPlayers.add(player);
    }
    return deadPlayers;
  }

  public static boolean shouldBeIncluded() {
    return !livesManager.getDeadPlayers().isEmpty();
  }

  public static boolean isRessurectedPlayer(ServerPlayerEntity player) {
    return ressurectedPlayers.contains(player.getUuid());
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.NECROMANCY;
  }

  @Override
  public int getCooldownMillis() {
    return 300000;
  }

  @Override
  public void activate() {
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }

    if (getDeadSpectatorPlayers().isEmpty()) {
      PlayerUtils.displayMessageToPlayer(player, Text.of("There are no dead players."), 80);
      return;
    }

    ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
    playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
        SoundEvents.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1, 1);

    List<ServerPlayerEntity> affectedPlayers = playerWorld.getEntitiesByClass(
        ServerPlayerEntity.class, player.getBoundingBox().expand(10),
        playerEntity -> playerEntity.distanceTo(player) <= 10);
    StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 115, 0);
    for (ServerPlayerEntity affectedPlayer : affectedPlayers) {
      affectedPlayer.addStatusEffect(blindness);
    }

    TaskScheduler.scheduleTask(100, () -> {
      ServerPlayerEntity updatedPlayer = getPlayer();
      if (updatedPlayer != null) {
        ServerWorld updatedPlayerWorld = PlayerUtils.getServerWorld(updatedPlayer);
        List<ServerPlayerEntity> deadPlayers = getDeadSpectatorPlayers();
        for (ServerPlayerEntity deadPlayer : deadPlayers) {
          BlockPos tpTo = WorldUitls.getCloseBlockPos(updatedPlayerWorld,
              updatedPlayer.getBlockPos(), 3, 2, true);
          PlayerUtils.teleport(deadPlayer, updatedPlayerWorld, tpTo);
          deadPlayer.changeGameMode(GameMode.SURVIVAL);
          if (seasonConfig instanceof WildLifeConfig config) {
            if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS.get(config)
                && !clearedPlayers.contains(deadPlayer.getUuid())) {
              clearedPlayers.add(deadPlayer.getUuid());
              deadPlayer.getInventory().clear();
            }
          }
          AttributeUtils.setMaxPlayerHealth(deadPlayer, 8);
          deadPlayer.setHealth(8);
          WorldUitls.summonHarmlessLightning(deadPlayer);
          ressurectedPlayers.add(deadPlayer.getUuid());
          perPlayerRessurections.add(deadPlayer.getUuid());
        }
      }
    });
    super.activate();
  }

  @Override
  public void deactivate() {
    super.deactivate();
    List<UUID> deadAgain = new ArrayList<>();
    for (ServerPlayerEntity player : livesManager.getDeadPlayers()) {
        if (player.isSpectator()) {
            continue;
        }
      UUID uuid = player.getUuid();
      if (perPlayerRessurections.contains(uuid) && ressurectedPlayers.contains(uuid)) {
        WorldUitls.summonHarmlessLightning(player);
        player.changeGameMode(GameMode.SPECTATOR);
        deadAgain.add(uuid);
      }
    }
    ressurectedPlayers.removeAll(deadAgain);
    perPlayerRessurections.removeAll(deadAgain);
  }

  @Override
  public void tick() {
    for (UUID uuid : perPlayerRessurections) {
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
      if (player != null && livesManager.isAlive(player)) {
        perPlayerRessurections.remove(uuid);
        ressurectedPlayers.remove(uuid);
        AttributeUtils.resetAttributesOnPlayerJoin(player);
      }
    }
  }
}
