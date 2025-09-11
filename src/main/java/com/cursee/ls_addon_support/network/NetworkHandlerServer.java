package com.cursee.ls_addon_support.network;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.DefaultConfigValues;
import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.network.packets.ConfigPayload;
import com.cursee.ls_addon_support.network.packets.HandshakePayload;
import com.cursee.ls_addon_support.network.packets.ImagePayload;
import com.cursee.ls_addon_support.network.packets.LongPayload;
import com.cursee.ls_addon_support.network.packets.NumberPayload;
import com.cursee.ls_addon_support.network.packets.PlayerDisguisePayload;
import com.cursee.ls_addon_support.network.packets.StringListPayload;
import com.cursee.ls_addon_support.network.packets.StringPayload;
import com.cursee.ls_addon_support.network.packets.TriviaQuestionPayload;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TripleJump;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PermissionManager;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.versions.VersionControl;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class NetworkHandlerServer {

  public static final List<UUID> handshakeSuccessful = new ArrayList<>();
  public static boolean updatedConfigThisTick = false;
  public static boolean configNeedsReload = false;

  public static void registerPackets() {
    PayloadTypeRegistry.playS2C().register(NumberPayload.ID, NumberPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(StringPayload.ID, StringPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(StringListPayload.ID, StringListPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(LongPayload.ID, LongPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(ImagePayload.ID, ImagePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);

    PayloadTypeRegistry.playC2S().register(NumberPayload.ID, NumberPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(StringPayload.ID, StringPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(StringListPayload.ID, StringListPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(HandshakePayload.ID, HandshakePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(LongPayload.ID, LongPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(ImagePayload.ID, ImagePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(ConfigPayload.ID, ConfigPayload.CODEC);
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
      ServerPlayerEntity player = context.player();
      MinecraftServer server = context.server();
      server.execute(() -> handleHandshakeResponse(player, payload));
    });
    ServerPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
      ServerPlayerEntity player = context.player();
      MinecraftServer server = context.server();
      server.execute(() -> handleNumberPacket(player, payload));
    });
    ServerPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
      ServerPlayerEntity player = context.player();
      MinecraftServer server = context.server();
      server.execute(() -> handleStringPacket(player, payload));
    });
    ServerPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
      ServerPlayerEntity player = context.player();
      MinecraftServer server = context.server();
      server.execute(() -> handleConfigPacket(player, payload));
    });
  }

  public static void handleConfigPacket(ServerPlayerEntity player, ConfigPayload payload) {
    if (PermissionManager.isAdmin(player)) {
      ConfigTypes configType = ConfigTypes.getFromString(payload.configType());
      String id = payload.id();
      List<String> args = payload.args();
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info(
                TextUtils.formatString(
                    "[PACKET_SERVER] Received config update from {}: {{}, {}, {}}",
                    player, configType, id, args));
        }

      if (configType.parentString() && !args.isEmpty()) {
        seasonConfig.setProperty(id, args.getFirst());
        updatedConfigThisTick = true;
      } else if (configType.parentBoolean() && !args.isEmpty()) {
        seasonConfig.setProperty(id, String.valueOf(args.getFirst().equalsIgnoreCase("true")));
        updatedConfigThisTick = true;
      } else if (configType.parentDouble() && !args.isEmpty()) {
        try {
          double value = Double.parseDouble(args.getFirst());
          seasonConfig.setProperty(id, String.valueOf(value));
          updatedConfigThisTick = true;
        } catch (Exception e) {
        }
      } else if (configType.parentInteger() && !args.isEmpty()) {
        try {
          int value = Integer.parseInt(args.getFirst());
          seasonConfig.setProperty(id, String.valueOf(value));
          updatedConfigThisTick = true;
        } catch (Exception e) {
        }
      }

      if (updatedConfigThisTick && DefaultConfigValues.RELOAD_NEEDED.contains(id)) {
        configNeedsReload = true;
      }
    }
  }

  public static void onUpdatedConfig() {
    PlayerUtils.broadcastMessageToAdmins(Text.of("§7Config has been successfully updated."));
    if (configNeedsReload) {
      PlayerUtils.broadcastMessageToAdmins(
          Text.of("Run §7'/lifeseries reload'§r to apply all the changes."));
    }
    updatedConfigThisTick = false;
    configNeedsReload = false;
    LSAddonSupport.softestReloadStart();
  }

  public static void handleNumberPacket(ServerPlayerEntity player, NumberPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    double value = payload.number();

    int intValue = (int) value;
    if (name == PacketNames.TRIVIA_ANSWER) {
        if (VersionControl.isDevVersion()) {
            LSAddonSupport.LOGGER.info(
                TextUtils.formatString("[PACKET_SERVER] Received trivia answer (from {}): {}",
                    player,
                    intValue));
        }
      TriviaWildcard.handleAnswer(player, intValue);
    }
  }

  public static void handleStringPacket(ServerPlayerEntity player, StringPayload payload) {
    String nameStr = payload.name();
    PacketNames name = PacketNames.fromName(nameStr);
    String value = payload.value();

    if (name == PacketNames.HOLDING_JUMP && currentSeason.getSeason() == Seasons.WILD_LIFE
        && WildcardManager.isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
      SizeShifting.onHoldingJump(player);
    }
    if (name == PacketNames.SUPERPOWER_KEY && currentSeason.getSeason() == Seasons.WILD_LIFE) {
      SuperpowersWildcard.pressedSuperpowerKey(player);
    }
    if (name == PacketNames.TRANSCRIPT) {
      player.sendMessage(SessionTranscript.getTranscriptMessage());
    }
    if (PermissionManager.isAdmin(player)) {
      if (name == PacketNames.SELECTED_WILDCARD) {
        String wildcard = Wildcards.getFromString(value);
        if (wildcard != null && !wildcard.equals(Wildcards.NULL)) {
          WildcardManager.chosenWildcard(wildcard);
        }
      }
      if (name == PacketNames.REQUEST_CONFIG) {
        seasonConfig.sendConfigTo(player);
      }
    }
    if (name == PacketNames.SET_SEASON) {
      if (PermissionManager.isAdmin(player) || currentSeason.getSeason() == Seasons.UNASSIGNED) {
        Seasons newSeason = Seasons.getSeasonFromStringName(value);
          if (newSeason == Seasons.UNASSIGNED) {
              return;
          }
        if (LSAddonSupport.changeSeasonTo(newSeason.getId())) {
          PlayerUtils.broadcastMessage(
              TextUtils.formatLoosely("§aSuccessfully changed the season to {}.", value));
        }
      }
    }
    if (name == PacketNames.REQUEST_SNAIL_MODEL) {
      if (Snails.snails.containsKey(player.getUuid())) {
        Snail snail = Snails.snails.get(player.getUuid());
        snail.updateModel(true);
      }
      if (TriviaWildcard.snails.containsKey(player.getUuid())) {
        Snail snail = TriviaWildcard.snails.get(player.getUuid());
        snail.updateModel(true);
      }
    }
    if (name == PacketNames.TRIPLE_JUMP) {
      if (currentSeason.getSeason() == Seasons.WILD_LIFE && SuperpowersWildcard.hasActivatedPower(
          player, Superpowers.TRIPLE_JUMP)) {
        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power instanceof TripleJump tripleJump) {
          tripleJump.isInAir = true;
        }
      }
    }
  }

  public static void handleHandshakeResponse(ServerPlayerEntity player, HandshakePayload payload) {
    String clientVersionStr = payload.modVersionStr();
    String clientCompatibilityStr = payload.compatibilityStr();
    String serverVersionStr = LSAddonSupport.MOD_VERSION;
    String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

    if (!LSAddonSupport.ISOLATED_ENVIROMENT) {
      int clientVersion = payload.modVersion();
      int clientCompatibility = payload.compatibility();
      int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
      int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

      //Check if client version is compatible with the server version
      if (clientVersion < serverCompatibility) {
        Text disconnectText = Text.literal("[Life Series Mod] Client-Server version mismatch!\n" +
            "Update the client version to at least version " + serverCompatibilityStr);
        player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
        return;
      }

      //Check if server version is compatible with the client version
      if (serverVersion < clientCompatibility) {
        Text disconnectText = Text.literal("[Life Series Mod] Server-Client version mismatch!\n" +
            "The client version is too new for the server.\n" +
            "Either update the server, or downgrade the client version to " + serverVersionStr);
        player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
        return;
      }
    } else {
      //Isolated enviroment -> mod versions must be IDENTICAL between client and server
      //Check if client version is the same as the server version
      if (!clientVersionStr.equalsIgnoreCase(serverVersionStr)) {
        Text disconnectText = Text.literal("[Life Series Mod] Client-Server version mismatch!\n" +
            "You must join with version " + serverCompatibilityStr);
        player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
        return;
      }
    }

    LSAddonSupport.LOGGER.info(
        TextUtils.formatString("[PACKET_SERVER] Received handshake (from {}): {{}, {}}", player,
            payload.modVersionStr(), payload.modVersion()));
    handshakeSuccessful.add(player.getUuid());
    PlayerUtils.resendCommandTree(player);
  }

  /*
      Sending
   */
  public static void sendTriviaPacket(ServerPlayerEntity player, String question, int difficulty,
      long timestamp, int timeToComplete, List<String> answers) {
    TriviaQuestionPayload triviaQuestionPacket = new TriviaQuestionPayload(question, difficulty,
        timestamp, timeToComplete, answers);
      if (VersionControl.isDevVersion()) {
          LSAddonSupport.LOGGER.info(TextUtils.formatString(
              "[PACKET_SERVER] Sending trivia question packet to {}): {{}, {}, {}, {}, {}}", player,
              question, difficulty, timestamp, timeToComplete, answers));
      }

    ServerPlayNetworking.send(player, triviaQuestionPacket);
  }

  public static void sendConfig(ServerPlayerEntity player, ConfigPayload configPacket) {
    ServerPlayNetworking.send(player, configPacket);
  }

  public static void sendHandshake(ServerPlayerEntity player) {
    String serverVersionStr = LSAddonSupport.MOD_VERSION;
    String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

    int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
    int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

    HandshakePayload payload = new HandshakePayload(serverVersionStr, serverVersion,
        serverCompatibilityStr, serverCompatibility);
    ServerPlayNetworking.send(player, payload);
    handshakeSuccessful.remove(player.getUuid());
      if (VersionControl.isDevVersion()) {
          LSAddonSupport.LOGGER.info(
              TextUtils.formatString("[PACKET_SERVER] Sending handshake to {}: {{}, {}}", player,
                  serverVersionStr, serverVersion));
      }

  }

  public static void sendStringPacket(ServerPlayerEntity player, PacketNames name, String value) {
    StringPayload payload = new StringPayload(name.getName(), value);
    ServerPlayNetworking.send(player, payload);
  }

  public static void sendStringListPacket(ServerPlayerEntity player, PacketNames name,
      List<String> value) {
    StringListPayload payload = new StringListPayload(name.getName(), value);
    ServerPlayNetworking.send(player, payload);
  }

  public static void sendStringListPackets(PacketNames name, List<String> value) {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      StringListPayload payload = new StringListPayload(name.getName(), value);
      ServerPlayNetworking.send(player, payload);
    }
  }

  public static void sendNumberPacket(ServerPlayerEntity player, PacketNames name, double number) {
      if (player == null) {
          return;
      }
    NumberPayload payload = new NumberPayload(name.getName(), number);
    ServerPlayNetworking.send(player, payload);
  }

  public static void sendLongPacket(ServerPlayerEntity player, PacketNames name, long number) {
      if (player == null) {
          return;
      }
    LongPayload payload = new LongPayload(name.getName(), number);
    ServerPlayNetworking.send(player, payload);
  }

  public static void sendImagePacket(ServerPlayerEntity player, ImagePayload payload) {
      if (player == null) {
          return;
      }
    ServerPlayNetworking.send(player, payload);
  }

  public static void sendImagePackets(ImagePayload payload) {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      ServerPlayNetworking.send(player, payload);
    }
  }

  public static void sendLongPackets(PacketNames name, long number) {
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      sendLongPacket(player, name, number);
    }
  }

  public static void sendUpdatePacketTo(ServerPlayerEntity player) {
    if (currentSeason instanceof WildLife) {
      sendNumberPacket(player, PacketNames.PLAYER_MIN_MSPT, TimeDilation.MIN_PLAYER_MSPT);

      List<String> activeWildcards = new ArrayList<>();
      for (String wildcard : WildcardManager.activeWildcards.keySet()) {
        activeWildcards.add(wildcard.toLowerCase());
      }
      sendStringPacket(player, PacketNames.ACTIVE_WILDCARDS, String.join("__", activeWildcards));
    }
    sendStringPacket(player, PacketNames.CURRENT_SEASON, currentSeason.getSeason().getId());
    sendStringPacket(player, PacketNames.TABLIST_SHOW_EXACT,
        String.valueOf(Season.TAB_LIST_SHOW_EXACT_LIVES));
  }

  public static void sendUpdatePackets() {
    PlayerUtils.getAllPlayers().forEach(NetworkHandlerServer::sendUpdatePacketTo);
  }

  public static void sendPlayerDisguise(String hiddenUUID, String hiddenName, String shownUUID,
      String shownName) {
    PlayerDisguisePayload payload = new PlayerDisguisePayload(PacketNames.PLAYER_DISGUISE.getName(),
        hiddenUUID, hiddenName, shownUUID, shownName);
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      ServerPlayNetworking.send(player, payload);
    }
  }

  public static void sendPlayerInvisible(UUID uuid, long timestamp) {
    LongPayload payload = new LongPayload(PacketNames.PLAYER_INVISIBLE.getName() + uuid.toString(),
        timestamp);
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
      ServerPlayNetworking.send(player, payload);
    }
  }

  public static void sendVignette(ServerPlayerEntity player, long durationMillis) {
    LongPayload payload = new LongPayload(PacketNames.SHOW_VIGNETTE.getName(), durationMillis);
    ServerPlayNetworking.send(player, payload);
  }

  public static void tryKickFailedHandshake(ServerPlayerEntity player) {
      if (server == null) {
          return;
      }
      if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
          return;
      }
      if (wasHandshakeSuccessful(player)) {
          return;
      }
    Text disconnectText = Text.literal(
            "You must have the §2Life Series mod\n§l installed on the client§r§r§f to play Wild Life!\n")
        .append(
            Text.literal("§9§nThe Life Series mod is available on Modrinth."));
    player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
  }

  public static boolean wasHandshakeSuccessful(ServerPlayerEntity player) {
      if (player == null) {
          return false;
      }
    return wasHandshakeSuccessful(player.getUuid());
  }

  public static boolean wasHandshakeSuccessful(UUID uuid) {
      if (uuid == null) {
          return false;
      }
    return NetworkHandlerServer.handshakeSuccessful.contains(uuid);
  }
}