package com.cursee.ls_addon_support.utils.player;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.livesManager;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.entity.fakeplayer.FakePlayer;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.other.WatcherManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.secretlife.SecretLife;
import com.cursee.ls_addon_support.seasons.session.Session;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.world.WorldUitls;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public class PlayerUtils {

  private static final Random rnd = new Random();
  public static List<UUID> updateInventoryQueue = new ArrayList<>();
  private static HashMap<Text, Integer> broadcastCooldown = new HashMap<>();

  public static void sendTitleWithSubtitle(ServerPlayerEntity player, Text title, Text subtitle,
      int fadeIn, int stay, int fadeOut) {
      if (server == null) {
          return;
      }
      if (player == null) {
          return;
      }
    if (player.isDead()) {
      TaskScheduler.scheduleTask(5,
          () -> sendTitleWithSubtitle(getPlayer(player.getUuid()), title, subtitle, fadeIn, stay,
              fadeOut));
      return;
    }
    TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
    player.networkHandler.sendPacket(fadePacket);
    TitleS2CPacket titlePacket = new TitleS2CPacket(title);
    player.networkHandler.sendPacket(titlePacket);
    SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(subtitle);
    player.networkHandler.sendPacket(subtitlePacket);
  }

  public static void sendTitle(ServerPlayerEntity player, Text title, int fadeIn, int stay,
      int fadeOut) {
      if (server == null) {
          return;
      }
      if (player == null) {
          return;
      }
    if (player.isDead()) {
      TaskScheduler.scheduleTask(5,
          () -> sendTitle(getPlayer(player.getUuid()), title, fadeIn, stay, fadeOut));
      return;
    }
    TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
    player.networkHandler.sendPacket(fadePacket);
    TitleS2CPacket titlePacket = new TitleS2CPacket(title);
    player.networkHandler.sendPacket(titlePacket);
  }

  public static void sendTitleToPlayers(Collection<ServerPlayerEntity> players, Text title,
      int fadeIn, int stay, int fadeOut) {
    for (ServerPlayerEntity player : players) {
      sendTitle(player, title, fadeIn, stay, fadeOut);
    }
  }

  public static void sendTitleWithSubtitleToPlayers(Collection<ServerPlayerEntity> players,
      Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
    for (ServerPlayerEntity player : players) {
      sendTitleWithSubtitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }
  }

  public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound) {
    playSoundToPlayers(players, sound, SoundCategory.MASTER, 1, 1);
  }

  public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound,
      float volume, float pitch) {
    playSoundToPlayers(players, sound, SoundCategory.MASTER, volume, pitch);
  }

  public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound,
      SoundCategory soundCategory, float volume, float pitch) {
    for (ServerPlayerEntity player : players) {
      player.playSoundToPlayer(sound, soundCategory, volume, pitch);
    }
  }

  public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound) {
    playSoundToPlayer(player, sound, 1, 1);
  }

  public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound, float volume,
      float pitch) {
    player.playSoundToPlayer(sound, SoundCategory.MASTER, volume, pitch);
  }

  public static void playSoundWithSourceToPlayers(Entity source, SoundEvent sound,
      SoundCategory soundCategory, float volume, float pitch) {
    playSoundWithSourceToPlayers(getAllPlayers(), source, sound, soundCategory, volume, pitch);
  }

  public static void playSoundWithSourceToPlayers(Collection<ServerPlayerEntity> players,
      Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
    PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(
        Registries.SOUND_EVENT.getEntry(sound), soundCategory, source, volume, pitch,
        rnd.nextLong());
    for (ServerPlayerEntity player : players) {
      player.networkHandler.sendPacket(packet);
    }
  }

  public static List<ServerPlayerEntity> getAllPlayers() {
    List<ServerPlayerEntity> result = new ArrayList<>();
      if (server == null) {
          return result;
      }

    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      if (!(player instanceof FakePlayer)) {
        result.add(player);
      }
    }
    return result;
  }

  public static List<ServerPlayerEntity> getAllFunctioningPlayers() {
    List<ServerPlayerEntity> result = getAllPlayers();
    result.removeIf(WatcherManager::isWatcher);
    return result;
  }

  public static ServerPlayerEntity getPlayer(String name) {
      if (server == null || name == null) {
          return null;
      }
    return server.getPlayerManager().getPlayer(name);
  }

  public static ServerPlayerEntity getPlayer(UUID uuid) {
      if (server == null || uuid == null) {
          return null;
      }
    return server.getPlayerManager().getPlayer(uuid);
  }

  public static void applyResourcepack(UUID uuid) {
      if (NetworkHandlerServer.wasHandshakeSuccessful(uuid)) {
          return;
      }
    applyServerResourcepack(uuid);
  }

  public static void applyServerResourcepack(UUID uuid) {
      if (server == null) {
          return;
      }
    ServerPlayerEntity player = getPlayer(uuid);
      if (player == null) {
          return;
      }
    applySingleResourcepack(player, Season.RESOURCEPACK_MAIN_URL, Season.RESOURCEPACK_MAIN_SHA,
        "Life Series Resourcepack.");
    applySingleResourcepack(player, Season.RESOURCEPACK_MINIMAL_ARMOR_URL,
        Season.RESOURCEPACK_MINIMAL_ARMOR_SHA, "Life Series Resourcepack.");
    if (currentSeason instanceof SecretLife) {
      applySingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL,
          Season.RESOURCEPACK_SECRETLIFE_SHA, "Life Series Resourcepack.");
    } else {
      removeSingleResourcepack(player, Season.RESOURCEPACK_SECRETLIFE_URL);
    }
  }

  private static void applySingleResourcepack(ServerPlayerEntity player, String link, String sha1,
      String message) {
    UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
    ResourcePackSendS2CPacket resourcepackPacket = new ResourcePackSendS2CPacket(
        id,
        link,
        sha1,
        false,
        Optional.of(Text.translatable(message))
    );
    player.networkHandler.sendPacket(resourcepackPacket);
  }

  private static void removeSingleResourcepack(ServerPlayerEntity player, String link) {
    UUID id = UUID.nameUUIDFromBytes(link.getBytes(StandardCharsets.UTF_8));
    ResourcePackRemoveS2CPacket removePackPacket = new ResourcePackRemoveS2CPacket(Optional.of(id));
    player.networkHandler.sendPacket(removePackPacket);
  }

  public static List<ItemStack> getPlayerInventory(ServerPlayerEntity player) {
    List<ItemStack> list = new ArrayList<>();
    Inventory inventory = player.getInventory();
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack itemStack = inventory.getStack(i);
      if (!itemStack.isEmpty()) {
        list.add(itemStack);
      }
    }
    return list;
  }

  public static void clearItemStack(ServerPlayerEntity player, ItemStack itemStack) {
      if (itemStack == null || itemStack.isEmpty()) {
          return;
      }
    Inventory inventory = player.getInventory();
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getStack(i);
      if (stack.equals(itemStack)) {
        inventory.removeStack(i);
      }
    }
  }

  public static Entity getEntityLookingAt(ServerPlayerEntity player, double maxDistance) {
    Vec3d start = player.getCameraPosVec(1.0F);
    Vec3d direction = player.getRotationVec(1.0F).normalize().multiply(maxDistance);
    Vec3d end = start.add(direction);

    HitResult entityHit = ProjectileUtil.raycast(player, start, end,
        player.getBoundingBox().stretch(direction).expand(1.0),
        entity -> !entity.isSpectator() && entity.isAlive(), maxDistance * maxDistance);

    if (entityHit instanceof EntityHitResult entityHitResult) {
      return entityHitResult.getEntity();
    }

    return null;
  }

  public static Vec3d getPosLookingAt(ServerPlayerEntity player, double maxDistance) {
    HitResult blockHit = player.raycast(maxDistance, 1, false);
    if (Math.sqrt(blockHit.squaredDistanceTo(player)) >= (maxDistance * 0.99)) {
      return null;
    }
    if (blockHit instanceof BlockHitResult blockHitResult) {
      return blockHitResult.getPos();
    }
    return null;
  }

  public static boolean isFakePlayer(PlayerEntity player) {
    return player instanceof FakePlayer;
  }

  public static void displayMessageToPlayer(ServerPlayerEntity player, Text text, int timeFor) {
    Session.skipTimer.put(player.getUuid(), timeFor / 5);
    player.sendMessage(text, true);
  }

  public static void updatePlayerInventory(ServerPlayerEntity player) {
      if (updateInventoryQueue.contains(player.getUuid())) {
          return;
      }
    updateInventoryQueue.add(player.getUuid());
  }

  public static void resendCommandTree(ServerPlayerEntity player) {
      if (player == null) {
          return;
      }
      if (player.getServer() == null) {
          return;
      }
    player.getServer().getCommandManager().sendCommandTree(player);
  }

  public static void resendCommandTrees() {
    for (ServerPlayerEntity player : getAllPlayers()) {
      resendCommandTree(player);
    }
  }

  public static ItemStack getEquipmentSlot(PlayerEntity player, int slot) {
      return player.getInventory().getStack(slot + 36);
  }

    public static List<ItemStack> getArmorItems(ServerPlayerEntity player) {
        List<ItemStack> result = new ArrayList<>();
        result.add(getEquipmentSlot(player, 0));
        result.add(getEquipmentSlot(player, 1));
        result.add(getEquipmentSlot(player, 2));
        result.add(getEquipmentSlot(player, 3));
        return result;
    }

  public static void updatePlayerLists() {
      if (server == null) {
          return;
      }
      if (currentSeason == null) {
          return;
      }

    List<ServerPlayerEntity> allPlayers = server.getPlayerManager().getPlayerList();

    for (ServerPlayerEntity receivingPlayer : allPlayers) {
      List<ServerPlayerEntity> visiblePlayers = new ArrayList<>();
      List<UUID> hiddenPlayerUUIDs = new ArrayList<>();
      List<String> hiddenPlayerNames = new ArrayList<>();

      for (ServerPlayerEntity player : allPlayers) {
          if (player == receivingPlayer) {
              continue;
          }

        boolean hidePlayer =
            !currentSeason.TAB_LIST_SHOW_DEAD_PLAYERS && livesManager.isAlive(receivingPlayer)
                && !livesManager.isAlive(player) && !WatcherManager.isWatcher(player);

        if (!currentSeason.WATCHERS_IN_TAB && !WatcherManager.isWatcher(receivingPlayer)
            && WatcherManager.isWatcher(player)) {
          hidePlayer = true;
        }

        if (hidePlayer) {
          hiddenPlayerUUIDs.add(player.getUuid());
          hiddenPlayerNames.add(player.getNameForScoreboard());
        } else {
          visiblePlayers.add(player);
        }
      }
      if (!visiblePlayers.isEmpty()) {
        receivingPlayer.networkHandler.sendPacket(
            PlayerListS2CPacket.entryFromPlayer(visiblePlayers));
      }
      if (!hiddenPlayerUUIDs.isEmpty()) {
        PlayerRemoveS2CPacket hidePacket = new PlayerRemoveS2CPacket(hiddenPlayerUUIDs);
        receivingPlayer.networkHandler.sendPacket(hidePacket);
      }

      NetworkHandlerServer.sendStringListPacket(receivingPlayer, PacketNames.UPDATE_HIDDEN_PLAYERS,
          hiddenPlayerNames);
    }
  }

  public static ServerWorld getServerWorld(ServerPlayerEntity player) {
      return player.getWorld();
  }

  public static void onTick() {
    if (!broadcastCooldown.isEmpty()) {
      HashMap<Text, Integer> newCooldowns = new HashMap<>();
      for (Map.Entry<Text, Integer> entry : broadcastCooldown.entrySet()) {
        Text key = entry.getKey();
        Integer value = entry.getValue();
        value--;
        if (value > 0) {
          newCooldowns.put(key, value);
        }
      }
      broadcastCooldown = newCooldowns;
    }

    if (!updateInventoryQueue.isEmpty()) {
      for (UUID uuid : updateInventoryQueue) {
        ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
          if (player == null) {
              continue;
          }

        player.getInventory().updateItems();
        player.currentScreenHandler.sendContentUpdates();
        if (!player.isCreative()) {
          player.currentScreenHandler.syncState();
          player.playerScreenHandler.syncState();
        }
      }
      updateInventoryQueue.clear();
    }
  }

  public static void broadcastMessage(Text message) {
    broadcastMessage(message, 1);
  }

  public static void broadcastMessageToAdmins(Text message) {
    broadcastMessageToAdmins(message, 1);
  }

  public static void broadcastMessageExcept(Text message, ServerPlayerEntity exceptPlayer) {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        if (player == exceptPlayer) {
            continue;
        }
      player.sendMessage(message, false);
    }
  }

  public static void broadcastMessage(Text message, int cooldownTicks) {
      if (broadcastCooldown.containsKey(message)) {
          return;
      }
    broadcastCooldown.put(message, cooldownTicks);

    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      player.sendMessage(message, false);
    }
  }

  public static void broadcastMessageToAdmins(Text message, int cooldownTicks) {
      if (broadcastCooldown.containsKey(message)) {
          return;
      }
    broadcastCooldown.put(message, cooldownTicks);

    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        if (!PermissionManager.isAdmin(player)) {
            continue;
        }
      player.sendMessage(message, false);
    }
    LSAddonSupport.LOGGER.info(message.getString());
  }

  public static void teleport(ServerPlayerEntity player, BlockPos pos) {
    teleport(player, getServerWorld(player), pos.toBottomCenterPos());
  }

  public static void teleport(ServerPlayerEntity player, Vec3d pos) {
    teleport(player, getServerWorld(player), pos);
  }

  public static void teleport(ServerPlayerEntity player, double destX, double destY, double destZ) {
    teleport(player, getServerWorld(player), destX, destY, destZ);
  }

  public static void teleport(ServerPlayerEntity player, ServerWorld world, double destX,
      double destY, double destZ) {
    teleport(player, world, destX, destY, destZ, player.getYaw(), player.getPitch());
  }

  public static void teleport(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
    teleport(player, world, pos.toBottomCenterPos());
  }

  public static void teleport(ServerPlayerEntity player, ServerWorld world, Vec3d pos) {
    teleport(player, world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
  }

  public static void teleport(ServerPlayerEntity player, ServerWorld world, Vec3d pos, float yaw,
      float pitch) {
    teleport(player, world, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
  }

  public static void teleport(ServerPlayerEntity player, ServerWorld world, double destX,
      double destY, double destZ, float yaw, float pitch) {
      player.teleport(world, destX, destY, destZ, EnumSet.noneOf(PositionFlag.class), yaw, pitch, false);
  }

  public static void safelyPutIntoSurvival(ServerPlayerEntity player) {
      if (player.interactionManager.getGameMode() == GameMode.SURVIVAL) {
          return;
      }

    //Teleport to the highest block in the terrain
    BlockPos.Mutable playerBlockPos = player.getBlockPos().mutableCopy();
    int safeY = WorldUitls.findTopSafeY(getServerWorld(player), playerBlockPos.toCenterPos());
    playerBlockPos.setY(safeY);
    teleport(player, playerBlockPos);

    player.changeGameMode(GameMode.SURVIVAL);
  }

  public static ServerPlayerEntity getPlayerOrProjection(ServerPlayerEntity player) {
      if (player == null) {
          return null;
      }
      if (!PlayerUtils.isFakePlayer(player)) {
          return player;
      }

    if (player instanceof FakePlayer fakePlayer) {
      return PlayerUtils.getPlayer(fakePlayer.shadow);
    }
    return player;
  }
}
