package com.cursee.ls_addon_support.events;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.features.SnailSkinsClient;
import com.cursee.ls_addon_support.gui.other.UpdateInfoScreen;
import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.utils.ClientResourcePacks;
import com.cursee.ls_addon_support.utils.ClientTaskScheduler;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.versions.UpdateChecker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

public class ClientEvents {

  public static long onGroundFor = 0;
  private static boolean hasShownUpdateScreen = false;
  private static int jumpedInAir = 0;
  private static int jumpCooldown = 0;
  private static boolean lastJumping = false;
  private static int invisibleSnailFor = 0;
  private static int invisibleTriviaSnailFor = 0;

  public static void registerClientEvents() {
    ClientPlayConnectionEvents.JOIN.register(ClientEvents::onClientJoin);
    ClientPlayConnectionEvents.DISCONNECT.register(ClientEvents::onClientDisconnect);
    ClientLifecycleEvents.CLIENT_STARTED.register(ClientEvents::onClientStart);
    ScreenEvents.AFTER_INIT.register(ClientEvents::onScreenOpen);
  }

  public static void onClientJoin(ClientPlayNetworkHandler handler, PacketSender sender,
      MinecraftClient client) {

  }

  public static void onClientDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
    LSAddonSupport.LOGGER.info("Client disconnected from server, clearing some client data.");
    LSAddonSupportClient.resetClientData();
  }

  public static void onScreenOpen(MinecraftClient client, Screen screen, int scaledWidth,
      int scaledHeight) {
    if (UpdateChecker.updateAvailable) {
      if (screen instanceof TitleScreen && !hasShownUpdateScreen) {
        client.execute(() -> {
          client.setScreen(
              new UpdateInfoScreen(UpdateChecker.versionName, UpdateChecker.versionDescription));
          hasShownUpdateScreen = true;
        });
      }
    }
  }

  public static void onClientStart(MinecraftClient client) {
  }

  public static void onClientTickEnd() {
    try {
      MinecraftClient client = MinecraftClient.getInstance();
      ClientPlayerEntity player = client.player;

      checkResourcepackReload();
      spawnInvisibilityParticles(client);
      if (player != null) {
        sendPackets(player);
        tryTripleJump(player);
        checkSnailInvisible(client, player);
        checkTriviaSnailInvisible(client, player);
        checkOnGroundFor(player);
      }
      ClientKeybinds.tick();
      ClientTaskScheduler.onClientTick();
    } catch (Exception ignored) {
    }
  }

  public static void checkOnGroundFor(ClientPlayerEntity player) {
    if (!player.isOnGround()) {
      onGroundFor = 0;
    } else {
      onGroundFor++;
    }
  }

  public static void spawnInvisibilityParticles(MinecraftClient client) {
      if (client.world == null) {
          return;
      }
      if (client.world.random.nextInt(15) != 0) {
          return;
      }
    for (PlayerEntity player : client.world.getPlayers()) {
      if (LSAddonSupportClient.invisiblePlayers.containsKey(player.getUuid())) {
        long time = LSAddonSupportClient.invisiblePlayers.get(player.getUuid());
        if (time > System.currentTimeMillis() || time == -1) {
          ParticleManager particleManager = client.particleManager;

          double x = player.getX() + (Math.random() - 0.5) * 0.6;
          double y = player.getY() + Math.random() * 1.8;
          double z = player.getZ() + (Math.random() - 0.5) * 0.6;

          ParticleEffect invisibilityParticle = TintedParticleEffect.create(
              ParticleTypes.ENTITY_EFFECT, 0x208891b5);
          particleManager.addParticle(invisibilityParticle, x, y, z, 0, 0, 0);
        }
      }
    }
  }

  public static void sendPackets(ClientPlayerEntity player) {
    if (LSAddonSupportClient.clientCurrentSeason == Seasons.WILD_LIFE
        && LSAddonSupportClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) {
        if (player.input.playerInput.jump()) {
            NetworkHandlerClient.sendHoldingJumpPacket();
        }
    }
  }

  public static void onClientJump(Entity entity) {
    if (entity instanceof ClientPlayerEntity) {
      MinecraftClient client = MinecraftClient.getInstance();
      ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
      jumpCooldown = 3;
    }
  }

  private static void tryTripleJump(ClientPlayerEntity player) {
    if (jumpCooldown > 0) {
      jumpCooldown--;
    }
    if (player.isOnGround()) {
      jumpedInAir = 0;
      return;
    }

      if (jumpedInAir >= 2) {
          return;
      }

    boolean shouldJump = false;
      boolean holdingJump = player.input.playerInput.jump();

    if (!lastJumping && holdingJump) {
      shouldJump = true;
    }
    lastJumping = holdingJump;
      if (!shouldJump) {
          return;
      }
      if (jumpCooldown > 0) {
          return;
      }

      if (!hasTripleJumpEffect(player)) {
          return;
      }
    jumpedInAir++;
    player.jump();
    player.playSoundToPlayer(SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(),
        SoundCategory.MASTER, 0.25f, 1f);
    NetworkHandlerClient.sendStringPacket(PacketNames.TRIPLE_JUMP, "");
  }

  private static boolean hasTripleJumpEffect(ClientPlayerEntity player) {
    for (Map.Entry<RegistryEntry<StatusEffect>, StatusEffectInstance> entry : player.getActiveStatusEffects()
        .entrySet()) {
        if (entry.getKey() != StatusEffects.JUMP_BOOST) {
            continue;
        }
      StatusEffectInstance jumpBoost = entry.getValue();
        if (jumpBoost.getAmplifier() != 2) {
            continue;
        }
        if (jumpBoost.getDuration() > 220 || jumpBoost.getDuration() < 200) {
            continue;
        }
      return true;
    }
    return false;
  }

  public static void checkSnailInvisible(MinecraftClient client, ClientPlayerEntity player) {
      if (client.world == null) {
          return;
      }
      if (LSAddonSupportClient.snailPos == null) {
          return;
      }
      if (LSAddonSupportClient.snailPosTime == 0) {
          return;
      }
      if (player.squaredDistanceTo(LSAddonSupportClient.snailPos.toCenterPos()) > 2500) {
          return;
      }
      if (System.currentTimeMillis() - LSAddonSupportClient.snailPosTime > 2000) {
          return;
      }
    if (invisibleSnailFor > 60) {
      invisibleSnailFor = 0;
      NetworkHandlerClient.sendStringPacket(PacketNames.REQUEST_SNAIL_MODEL, "");
    }

    List<Entity> snailEntities = new ArrayList<>();
    for (DisplayEntity.ItemDisplayEntity entity : client.world.getEntitiesByClass(
        DisplayEntity.ItemDisplayEntity.class,
        new Box(LSAddonSupportClient.snailPos).expand(10), entity -> true)) {
      if (LSAddonSupportClient.snailPartUUIDs.contains(entity.getUuid())) {
        snailEntities.add(entity);
      }
    }

    if (snailEntities.isEmpty()) {
      invisibleSnailFor++;
    } else {
      invisibleSnailFor = 0;
    }
  }

  public static void checkTriviaSnailInvisible(MinecraftClient client, ClientPlayerEntity player) {
      if (client.world == null) {
          return;
      }
      if (LSAddonSupportClient.triviaSnailPos == null) {
          return;
      }
      if (LSAddonSupportClient.triviaSnailPosTime == 0) {
          return;
      }
      if (player.squaredDistanceTo(LSAddonSupportClient.triviaSnailPos.toCenterPos()) > 2500) {
          return;
      }
      if (System.currentTimeMillis() - LSAddonSupportClient.triviaSnailPosTime > 2000) {
          return;
      }
    if (invisibleTriviaSnailFor > 60) {
      invisibleTriviaSnailFor = 0;
      NetworkHandlerClient.sendStringPacket(PacketNames.REQUEST_SNAIL_MODEL, "");
    }

    List<Entity> snailEntities = new ArrayList<>();
    for (DisplayEntity.ItemDisplayEntity entity : client.world.getEntitiesByClass(
        DisplayEntity.ItemDisplayEntity.class,
        new Box(LSAddonSupportClient.triviaSnailPos).expand(10), entity -> true)) {
      if (LSAddonSupportClient.triviaSnailPartUUIDs.contains(entity.getUuid())) {
        snailEntities.add(entity);
      }
    }

    if (snailEntities.isEmpty()) {
      invisibleTriviaSnailFor++;
    } else {
      invisibleTriviaSnailFor = 0;
    }
  }

  public static void checkResourcepackReload() {
      if (SnailSkinsClient.skinReloadTicks <= 0) {
          return;
      }
    SnailSkinsClient.skinReloadTicks--;
    if (SnailSkinsClient.skinReloadTicks == 0) {
      ClientResourcePacks.enableClientResourcePack(ClientResourcePacks.SNAILS_RESOURCEPACK, true);
    }
  }
}
