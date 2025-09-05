package com.cursee.ls_addon_support.network;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.config.ClientConfig;
import com.cursee.ls_addon_support.config.ClientConfigGuiManager;
import com.cursee.ls_addon_support.config.ClientConfigNetwork;
import com.cursee.ls_addon_support.features.SnailSkinsClient;
import com.cursee.ls_addon_support.features.Trivia;
import com.cursee.ls_addon_support.gui.other.ChooseWildcardScreen;
import com.cursee.ls_addon_support.gui.other.SnailTextureInfoScreen;
import com.cursee.ls_addon_support.gui.seasons.ChooseSeasonScreen;
import com.cursee.ls_addon_support.gui.seasons.SeasonInfoScreen;
import com.cursee.ls_addon_support.network.packets.ConfigPayload;
import com.cursee.ls_addon_support.network.packets.HandshakePayload;
import com.cursee.ls_addon_support.network.packets.ImagePayload;
import com.cursee.ls_addon_support.network.packets.LongPayload;
import com.cursee.ls_addon_support.network.packets.NumberPayload;
import com.cursee.ls_addon_support.network.packets.PlayerDisguisePayload;
import com.cursee.ls_addon_support.network.packets.StringListPayload;
import com.cursee.ls_addon_support.network.packets.StringPayload;
import com.cursee.ls_addon_support.network.packets.TriviaQuestionPayload;
import com.cursee.ls_addon_support.render.VignetteRenderer;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import com.cursee.ls_addon_support.seasons.session.SessionStatus;
import com.cursee.ls_addon_support.utils.ClientResourcePacks;
import com.cursee.ls_addon_support.utils.ClientTaskScheduler;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.versions.VersionControl;
import com.cursee.ls_addon_support.utils.world.AnimationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class NetworkHandlerClient {

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleNumberPacket(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleStringPacket(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleHandshake(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(TriviaQuestionPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> Trivia.receiveTrivia(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(LongPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleLongPacket(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handlePlayerDisguise(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(ImagePayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleImagePacket(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleConfigPacket(payload));
    });
    ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
      MinecraftClient client = context.client();
      client.execute(() -> handleStringListPacket(payload));
    });
  }

  public static void handleStringListPacket(StringListPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    List<String> value = payload.value();

    if (name == PacketNames.MORPH) {
      try {
        String morphUUIDStr = value.get(0);
        UUID morphUUID = UUID.fromString(morphUUIDStr);
        String morphTypeStr = value.get(1);
        EntityType<?> morphType = null;
        if (!morphTypeStr.equalsIgnoreCase("null") && !morphUUIDStr.isEmpty()) {
          morphType = Registries.ENTITY_TYPE.get(Identifier.of(morphTypeStr));
        }
          if (VersionControl.isDevVersion()) {
              LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Received morph packet: {} ({})",
                  morphType,
                  morphUUID);
          }
        MorphManager.setFromPacket(morphUUID, morphType);
      } catch (Exception e) {
      }
    }
    if (name == PacketNames.UPDATE_HIDDEN_PLAYERS) {
      LSAddonSupportClient.hiddenTabPlayers = new ArrayList<>(value);
    }
  }

  public static void handleConfigPacket(ConfigPayload payload) {
    ClientConfigNetwork.handleConfigPacket(payload, false);
  }

  public static void handleImagePacket(ImagePayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    if (name == PacketNames.SNAIL_SKIN) {
      SnailSkinsClient.handleSnailSkin(payload);
    }
  }

  public static void handleStringPacket(StringPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    String value = payload.value();

    if (name == PacketNames.CURRENT_SEASON) {
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Updated current season to {}", value);
        }
      LSAddonSupportClient.clientCurrentSeason = Seasons.getSeasonFromStringName(value);
      ClientResourcePacks.checkClientPacks();
      LSAddonSupportClient.reloadConfig();
    }

    if (name == PacketNames.SESSION_STATUS) {
      LSAddonSupportClient.clientSessionStatus = SessionStatus.getSessionName(value);
    }

    if (name == PacketNames.ACTIVE_WILDCARDS) {
      List<Wildcards> newList = new ArrayList<>();
      for (String wildcardStr : value.split("__")) {
        newList.add(Wildcards.getFromString(wildcardStr));
      }
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);
        }
      LSAddonSupportClient.clientActiveWildcards = newList;
    }

    if (name == PacketNames.JUMP && MinecraftClient.getInstance().player != null) {
      MinecraftClient.getInstance().player.jump();
    }

    if (name == PacketNames.RESET_TRIVIA) {
      Trivia.resetTrivia();
    }

    if (name == PacketNames.SELECT_WILDCARDS) {
      MinecraftClient.getInstance().setScreen(new ChooseWildcardScreen());
    }

    if (name == PacketNames.OPEN_CONFIG) {
      ClientConfigNetwork.load();
      ClientTaskScheduler.scheduleTask(10, ClientConfigGuiManager::openConfig);
    }

    if (name == PacketNames.SELECT_SEASON) {
      MinecraftClient.getInstance().setScreen(new ChooseSeasonScreen(!value.isEmpty()));
    }
    if (name == PacketNames.SEASON_INFO) {
      Seasons season = Seasons.getSeasonFromStringName(value);
        if (season != Seasons.UNASSIGNED) {
            MinecraftClient.getInstance().setScreen(new SeasonInfoScreen(season));
        }
    }

    if (name == PacketNames.TRIVIA_BOT_PART) {
      try {
        UUID uuid = UUID.fromString(value);
        LSAddonSupportClient.triviaBotPartUUIDs.add(uuid);
      } catch (Exception e) {
      }
    }

    if (name == PacketNames.SNAIL_PART) {
      try {
        UUID uuid = UUID.fromString(value);
        LSAddonSupportClient.snailPartUUIDs.add(uuid);
      } catch (Exception e) {
      }
    }

    if (name == PacketNames.SNAIL_POS) {
      try {
        String[] split = value.split("_");
        BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
            Integer.parseInt(split[2]));
        LSAddonSupportClient.snailPos = pos;
        LSAddonSupportClient.snailPosTime = System.currentTimeMillis();
      } catch (Exception e) {
      }
    }

    if (name == PacketNames.TRIVIA_SNAIL_PART) {
      try {
        UUID uuid = UUID.fromString(value);
        LSAddonSupportClient.triviaSnailPartUUIDs.add(uuid);
      } catch (Exception e) {
      }
    }

    if (name == PacketNames.TRIVIA_SNAIL_POS) {
      try {
        String[] split = value.split("_");
        BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
            Integer.parseInt(split[2]));
        LSAddonSupportClient.triviaSnailPos = pos;
        LSAddonSupportClient.triviaSnailPosTime = System.currentTimeMillis();
      } catch (Exception e) {
      }
    }

    if (name == PacketNames.SNAIL_TEXTURES_INFO) {
      MinecraftClient.getInstance().setScreen(new SnailTextureInfoScreen());
    }

    if (name == PacketNames.PREVENT_GLIDING) {
      LSAddonSupportClient.preventGliding = value.equalsIgnoreCase("true");
    }

    if (name == PacketNames.TOGGLE_TIMER) {
      String key = ClientConfig.SESSION_TIMER.key;
      if (LSAddonSupportClient.clientCurrentSeason == Seasons.LIMITED_LIFE) {
        key = ClientConfig.SESSION_TIMER_LIMITEDLIFE.key;
      }
      LSAddonSupportClient.clientConfig.setProperty(key,
          String.valueOf(!LSAddonSupportClient.SESSION_TIMER));
      LSAddonSupportClient.reloadConfig();
    }
    if (name == PacketNames.TABLIST_SHOW_EXACT) {
      LSAddonSupportClient.TAB_LIST_SHOW_EXACT_LIVES = value.equalsIgnoreCase("true");
    }
    if (name == PacketNames.SHOW_TOTEM) {
      ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultStack();
      if (value.equalsIgnoreCase("task") || value.equalsIgnoreCase("task_red")) {
        totemItem = AnimationUtils.getSecretLifeTotemItem(value.equalsIgnoreCase("task_red"));
      }
      MinecraftClient.getInstance().gameRenderer.showFloatingItem(totemItem);
    }
  }

  public static void handleNumberPacket(NumberPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    double number = payload.number();

    int intNumber = (int) number;
    if (name == PacketNames.PLAYER_MIN_MSPT) {
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", number);
        }
      TimeDilation.MIN_PLAYER_MSPT = (float) number;
    }
    if (name == PacketNames.SNAIL_AIR) {
      LSAddonSupportClient.snailAir = intNumber;
      LSAddonSupportClient.snailAirTimestamp = System.currentTimeMillis();
    }
    if (name == PacketNames.FAKE_THUNDER && MinecraftClient.getInstance().world != null) {
      MinecraftClient.getInstance().world.setLightningTicksLeft(intNumber);
    }
  }

  public static void handleLongPacket(LongPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    long number = payload.number();

    if (name == PacketNames.SUPERPOWER_COOLDOWN) {
      LSAddonSupportClient.SUPERPOWER_COOLDOWN_TIMESTAMP = number;
    }
    if (name == PacketNames.SHOW_VIGNETTE) {
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", number);
        }
      VignetteRenderer.showVignetteFor(0.35f, number);
    }
    if (name == PacketNames.MIMICRY_COOLDOWN) {
      LSAddonSupportClient.MIMICRY_COOLDOWN_TIMESTAMP = number;
    }
    if (nameStr.startsWith(PacketNames.PLAYER_INVISIBLE.getName())) {
      try {
        UUID uuid = UUID.fromString(
            nameStr.replaceFirst(PacketNames.PLAYER_INVISIBLE.getName(), ""));
        if (number == 0) {
          LSAddonSupportClient.invisiblePlayers.remove(uuid);
        } else {
          LSAddonSupportClient.invisiblePlayers.put(uuid, number);
        }
      } catch (Exception ignored) {
      }
    }

    if (name == PacketNames.TIME_DILATION) {
      LSAddonSupportClient.TIME_DILATION_TIMESTAMP = number;
    }

    if (name == PacketNames.SESSION_TIMER) {
      LSAddonSupportClient.sessionTime = number;
      LSAddonSupportClient.sessionTimeLastUpdated = System.currentTimeMillis();
    }

    if (nameStr.startsWith(PacketNames.LIMITED_LIFE_TIMER.getName())) {
      LSAddonSupportClient.limitedLifeTimerColor = nameStr.replaceFirst(
          PacketNames.LIMITED_LIFE_TIMER.getName(), "");
      LSAddonSupportClient.limitedLifeLives = number;
      LSAddonSupportClient.limitedLifeTimeLastUpdated = System.currentTimeMillis();
    }

    if (name == PacketNames.CURSE_SLIDING) {
      LSAddonSupportClient.CURSE_SLIDING = number;
    }
  }

  public static void handlePlayerDisguise(PlayerDisguisePayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);

    String hiddenUUID = payload.hiddenUUID();
    String hiddenName = payload.hiddenName();
    String shownUUID = payload.shownUUID();
    String shownName = payload.shownName();

    if (name == PacketNames.PLAYER_DISGUISE) {
      if (shownName.isEmpty()) {
        LSAddonSupportClient.playerDisguiseNames.remove(hiddenName);
        try {
          UUID hideUUID = UUID.fromString(hiddenUUID);
          LSAddonSupportClient.playerDisguiseUUIDs.remove(hideUUID);
        } catch (Exception ignored) {
        }
      } else {
        LSAddonSupportClient.playerDisguiseNames.put(hiddenName, shownName);
        try {
          UUID hideUUID = UUID.fromString(hiddenUUID);
          UUID showUUID = UUID.fromString(shownUUID);
          LSAddonSupportClient.playerDisguiseUUIDs.put(hideUUID, showUUID);
        } catch (Exception ignored) {
        }
      }

    }
  }

  public static void handleHandshake(HandshakePayload payload) {
    LSAddonSupport.LOGGER.info(
        TextUtils.formatString("[PACKET_CLIENT] Received handshake (from server): {{}, {}}",
            payload.modVersionStr(), payload.modVersion()));
    sendHandshake();
  }

  public static void sendHandshake() {
    String clientVersionStr = LSAddonSupport.MOD_VERSION;
    String clientCompatibilityStr = VersionControl.clientCompatibilityMin();

    int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
    int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

    HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion,
        clientCompatibilityStr, clientCompatibility);
    ClientPlayNetworking.send(sendPayload);
      if (VersionControl.isDevVersion()) {
          LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Sent handshake");
      }
  }

    /*
        Sending
     */

  public static void sendConfigUpdate(String configType, String id, List<String> args) {
    ConfigPayload configPacket = new ConfigPayload(configType, id, -1, "", "", args);
    ClientPlayNetworking.send(configPacket);
  }

  public static void sendTriviaAnswer(int answer) {
      if (VersionControl.isDevVersion()) {
          LSAddonSupport.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
      }
    ClientPlayNetworking.send(new NumberPayload(PacketNames.TRIVIA_ANSWER.getName(), answer));
  }

  public static void sendHoldingJumpPacket() {
    ClientPlayNetworking.send(new StringPayload(PacketNames.HOLDING_JUMP.getName(), "true"));
  }

  public static void pressSuperpowerKey() {
    ClientPlayNetworking.send(new StringPayload(PacketNames.SUPERPOWER_KEY.getName(), "true"));
  }

  public static void pressRunCommandKey() {
      if (MinecraftClient.getInstance().getNetworkHandler() == null) {
          return;
      }
    MinecraftClient.getInstance().getNetworkHandler()
        .sendChatCommand(LSAddonSupportClient.RUN_COMMAND);
  }

  public static void sendStringPacket(PacketNames name, String value) {
    ClientPlayNetworking.send(new StringPayload(name.getName(), value));
  }

  public static void sendNumberPacket(PacketNames name, double value) {
    ClientPlayNetworking.send(new NumberPayload(name.getName(), value));
  }
}
