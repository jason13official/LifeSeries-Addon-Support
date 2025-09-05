package com.cursee.ls_addon_support.events;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.entity.fakeplayer.FakePlayer;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.resources.datapack.DatapackManager;
import com.cursee.ls_addon_support.seasons.other.WatcherManager;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.seasons.season.secretlife.SecretLife;
import com.cursee.ls_addon_support.seasons.season.secretlife.TaskManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.versions.UpdateChecker;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Events {

  /*
      Non-events
   */
  public static final List<UUID> joiningPlayers = new ArrayList<>();
  private static final Map<UUID, Vec3d> joiningPlayersPos = new HashMap<>();
  private static final Map<UUID, Float> joiningPlayersYaw = new HashMap<>();
  private static final Map<UUID, Float> joiningPlayersPitch = new HashMap<>();
  public static boolean skipNextTickReload = false;
  public static boolean updatePlayerListsNextTick = false;

  public static void register() {
    ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
    ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
    ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);

    ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(Events::onReloadStart);
    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(Events::onReloadEnd);

    AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
      if (!(player instanceof ServerPlayerEntity)) {
        return ActionResult.PASS; // Only handle server-side events
      }

      return Events.onBlockAttack((ServerPlayerEntity) player, world, pos);
    });
    UseBlockCallback.EVENT.register(Events::onBlockUse);
    UseItemCallback.EVENT.register(Events::onItemUse);
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> onPlayerJoin(handler.getPlayer()));
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> onPlayerDisconnect(handler.getPlayer()));
    ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);

    ServerLivingEntityEvents.AFTER_DEATH.register(Events::onEntityDeath);
    UseEntityCallback.EVENT.register(Events::onRightClickEntity);
    AttackEntityCallback.EVENT.register(Events::onAttackEntity);
  }

  private static void onReloadStart(MinecraftServer server,
      LifecycledResourceManager resourceManager) {
    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      LSAddonSupport.reloadStart();
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onReloadEnd(MinecraftServer server, LifecycledResourceManager resourceManager,
      boolean success) {
    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      LSAddonSupport.reloadEnd();
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onPlayerJoin(ServerPlayerEntity player) {
      if (isFakePlayer(player)) {
          return;
      }

    try {
      playerStartJoining(player);
      currentSeason.onPlayerJoin(player);
      currentSeason.onUpdatedInventory(player);
      SessionTranscript.playerJoin(player);
      MorphManager.onPlayerJoin(player);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onPlayerFinishJoining(ServerPlayerEntity player) {
      if (isFakePlayer(player)) {
          return;
      }

    try {
      UpdateChecker.onPlayerJoin(player);
      currentSeason.onPlayerFinishJoining(player);
      TaskScheduler.scheduleTask(10, () -> {
        NetworkHandlerServer.tryKickFailedHandshake(player);
        PlayerUtils.resendCommandTree(player);
      });
      MorphManager.onPlayerDisconnect(player);
      MorphManager.syncToPlayer(player);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onPlayerDisconnect(ServerPlayerEntity player) {
      if (isFakePlayer(player)) {
          return;
      }

    try {
      currentSeason.onPlayerDisconnect(player);
      SessionTranscript.playerLeave(player);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onServerStopping(MinecraftServer server) {
    try {
      UpdateChecker.shutdownExecutor();
      currentSession.sessionEnd();
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onServerStarting(MinecraftServer server) {
    LSAddonSupport.server = server;
  }

  private static void onServerStart(MinecraftServer server) {
    try {
      LSAddonSupport.server = server;
      currentSeason.initialize();
      blacklist.reloadBlacklist();
      if (currentSeason.getSeason() == Seasons.DOUBLE_LIFE) {
        ((DoubleLife) currentSeason).loadSoulmates();
      }
      DatapackManager.onServerStarted(server);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  private static void onServerTickEnd(MinecraftServer server) {
    try {
      skipNextTickReload = false;
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      if (updatePlayerListsNextTick) {
        updatePlayerListsNextTick = false;
        PlayerUtils.updatePlayerLists();
      }
      checkPlayerFinishJoiningTick();
        if (server.getTickManager().isFrozen()) {
            return;
        }
      if (LSAddonSupport.currentSession != null) {
        LSAddonSupport.currentSession.tick(server);
        currentSeason.tick(server);
      }
      PlayerUtils.onTick();
      if (NetworkHandlerServer.updatedConfigThisTick) {
        NetworkHandlerServer.onUpdatedConfig();
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  public static void onEntityDeath(LivingEntity entity, DamageSource source) {
      if (isFakePlayer(entity)) {
          return;
      }
    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      if (entity instanceof ServerPlayerEntity player) {
        Events.onPlayerDeath(player, source);
        return;
      }
      currentSeason.onMobDeath(entity, source);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  public static void onEntityDropItems(LivingEntity entity, DamageSource source) {
      if (isFakePlayer(entity)) {
          return;
      }
    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      currentSeason.onEntityDropItems(entity, source);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  public static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
      if (isExcludedPlayer(player)) {
          return;
      }

    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return;
        }
      currentSeason.onPlayerDeath(player, source);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
  }

  public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand,
      BlockHitResult hitResult) {
      if (isFakePlayer(player)) {
          return ActionResult.PASS;
      }

    if (player instanceof ServerPlayerEntity serverPlayer &&
        world instanceof ServerWorld serverWorld && LSAddonSupport.isLogicalSide()) {
      try {
        if (currentSeason instanceof SecretLife) {
          TaskManager.onBlockUse(
              serverPlayer,
              serverWorld,
              hitResult);
        }
          if (blacklist == null) {
              return ActionResult.PASS;
          }
        return blacklist.onBlockUse(serverPlayer, serverWorld, hand, hitResult);
      } catch (Exception e) {
        LSAddonSupport.LOGGER.error(e.getMessage());
        return ActionResult.PASS;
      }
    }
    return ActionResult.PASS;
  }

  public static ActionResult onItemUse(PlayerEntity player, World world, Hand hand) {
      if (isFakePlayer(player)) {
          return ActionResult.PASS;
      }

    if (player instanceof ServerPlayerEntity serverPlayer &&
        world instanceof ServerWorld serverWorld && LSAddonSupport.isLogicalSide()) {
      try {
        ItemStack itemStack = player.getStackInHand(hand);
        if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(serverPlayer, 3),
            "FlightSuperpower") &&
            itemStack.isOf(Items.FIREWORK_ROCKET)) {
          return ActionResult.FAIL;
        }
      } catch (Exception e) {
        LSAddonSupport.LOGGER.error(e.getMessage());
        return ActionResult.PASS;
      }
    }
    return ActionResult.PASS;
  }

  public static ActionResult onBlockAttack(ServerPlayerEntity player, World world, BlockPos pos) {
      if (isFakePlayer(player)) {
          return ActionResult.PASS;
      }

    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return ActionResult.PASS;
        }
        if (blacklist == null) {
            return ActionResult.PASS;
        }
        if (world.isClient()) {
            return ActionResult.PASS;
        }
      return blacklist.onBlockAttack(player, world, pos);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
      return ActionResult.PASS;
    }
  }

  private static ActionResult onRightClickEntity(PlayerEntity player, World world, Hand hand,
      Entity entity, EntityHitResult hitResult) {
      if (isFakePlayer(player)) {
          return ActionResult.PASS;
      }

    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return ActionResult.PASS;
        }
      if (player instanceof ServerPlayerEntity serverPlayer) {
        currentSeason.onRightClickEntity(serverPlayer, world, hand, entity, hitResult);
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
    return ActionResult.PASS;
  }

  private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand,
      Entity entity, EntityHitResult hitResult) {
      if (isFakePlayer(player)) {
          return ActionResult.PASS;
      }

    try {
        if (!LSAddonSupport.isLogicalSide()) {
            return ActionResult.PASS;
        }
      if (player instanceof ServerPlayerEntity serverPlayer) {
        currentSeason.onAttackEntity(serverPlayer, world, hand, entity, hitResult);
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error(e.getMessage());
    }
    return ActionResult.PASS;
  }

  public static void playerStartJoining(ServerPlayerEntity player) {
    NetworkHandlerServer.sendHandshake(player);
    NetworkHandlerServer.sendUpdatePacketTo(player);
    SnailSkinsServer.sendStoredImages(List.of(player));
    joiningPlayers.add(player.getUuid());
    joiningPlayersPos.put(player.getUuid(), player.getPos());
    joiningPlayersYaw.put(player.getUuid(), player.getYaw());
    joiningPlayersPitch.put(player.getUuid(), player.getPitch());
  }

  public static void checkPlayerFinishJoiningTick() {
    for (Map.Entry<UUID, Vec3d> entry : joiningPlayersPos.entrySet()) {
      UUID uuid = entry.getKey();
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
        if (player == null) {
            continue;
        }
        if (player.getPos().equals(entry.getValue())) {
            continue;
        }
      onPlayerFinishJoining(player);
      finishedJoining(player.getUuid());
      return;
    }
    //Yaw
    for (Map.Entry<UUID, Float> entry : joiningPlayersYaw.entrySet()) {
      UUID uuid = entry.getKey();
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
        if (player == null) {
            continue;
        }
        if (player.getYaw() == entry.getValue()) {
            continue;
        }
      onPlayerFinishJoining(player);
      finishedJoining(player.getUuid());
      return;
    }
    //Pitch
    for (Map.Entry<UUID, Float> entry : joiningPlayersPitch.entrySet()) {
      UUID uuid = entry.getKey();
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
        if (player == null) {
            continue;
        }
        if (player.getPitch() == entry.getValue()) {
            continue;
        }
      onPlayerFinishJoining(player);
      finishedJoining(player.getUuid());
      return;
    }

  }

  public static void finishedJoining(UUID uuid) {
    joiningPlayers.remove(uuid);
    joiningPlayersPos.remove(uuid);
    joiningPlayersYaw.remove(uuid);
    joiningPlayersPitch.remove(uuid);
  }

  public static boolean isExcludedPlayer(Entity entity) {
    if (entity instanceof ServerPlayerEntity player) {
      if (WatcherManager.isWatcher(player)) {
        return true;
      }
    }
    return isFakePlayer(entity);
  }

  public static boolean isFakePlayer(Entity entity) {
    return entity instanceof FakePlayer;
  }
}
