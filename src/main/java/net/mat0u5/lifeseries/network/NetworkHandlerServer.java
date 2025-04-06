package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.client.config.BooleanObject;
import net.mat0u5.lifeseries.client.config.DoubleObject;
import net.mat0u5.lifeseries.client.config.IntegerObject;
import net.mat0u5.lifeseries.client.config.StringObject;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.TripleJump;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PermissionManager;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.VersionControl;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class NetworkHandlerServer {
    public static final List<UUID> handshakeSuccessful = new ArrayList<>();

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StringPayload.ID, StringPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TriviaQuestionPayload.ID, TriviaQuestionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LongPayload.ID, LongPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDisguisePayload.ID, PlayerDisguisePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ImagePayload.ID, ImagePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(NumberPayload.ID, NumberPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StringPayload.ID, StringPayload.CODEC);
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
            server.execute(() -> handleNumberPacket(player, payload.name(), payload.number()));
        });
        ServerPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleStringPacket(player, payload.name(),payload.value()));
        });
        ServerPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleConfigPacket(player, payload));
        });
    }

    public static boolean updatedConfigThisTick = false;
    public static void handleConfigPacket(ServerPlayerEntity player, ConfigPayload payload) {
        if (PermissionManager.isAdmin(player)) {
            String configType = payload.configType();
            int index = payload.index();
            String id = payload.id();
            String name = payload.name();
            String description = payload.description();
            List<String> args = payload.args();

            if (configType.equalsIgnoreCase("string") && !args.isEmpty()) {
                seriesConfig.setProperty(id, args.getFirst());
                updatedConfigThisTick = true;
                return;
            }
            if (configType.equalsIgnoreCase("boolean") && !args.isEmpty()) {
                seriesConfig.setProperty(id, String.valueOf(args.getFirst().equalsIgnoreCase("true")));
                updatedConfigThisTick = true;
                return;
            }
            if (configType.equalsIgnoreCase("double") && !args.isEmpty()) {
                try {
                    double value = Double.parseDouble(args.getFirst());
                    seriesConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                    return;
                }catch(Exception e){}
            }
            if (configType.equalsIgnoreCase("integer") && !args.isEmpty()) {
                try {
                    int value = Integer.parseInt(args.getFirst());
                    seriesConfig.setProperty(id, String.valueOf(value));
                    updatedConfigThisTick = true;
                    return;
                }catch(Exception e){}
            }
        }
    }

    public static void onUpdatedConfig() {
        updatedConfigThisTick = false;
        OtherUtils.broadcastMessageToAdmins(Text.of("§7Config Updated. Run §f'/lifeseries reload'§7 to apply the changes."));
    }

    public static void handleNumberPacket(ServerPlayerEntity player, String name, double value) {
        int intValue = (int) value;
        if (name.equalsIgnoreCase("trivia_answer")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_SERVER] Received trivia answer (from "+player.getNameForScoreboard()+"): "+ intValue);
            TriviaWildcard.handleAnswer(player, intValue);
        }
    }
    public static void handleStringPacket(ServerPlayerEntity player, String name, String value) {
        if (name.equalsIgnoreCase("holding_jump") && currentSeries.getSeries() == SeriesList.WILD_LIFE && WildcardManager.isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
            SizeShifting.onHoldingJump(player);
        }
        if (name.equalsIgnoreCase("superpower_key") && currentSeries.getSeries() == SeriesList.WILD_LIFE) {
            SuperpowersWildcard.pressedSuperpowerKey(player);
        }
        if (PermissionManager.isAdmin(player)) {
            if (name.equalsIgnoreCase("selected_wildcard")) {
                Wildcards wildcard = Wildcards.getFromString(value);
                if (wildcard != null && wildcard != Wildcards.NULL) {
                    WildcardManager.chosenWildcard(wildcard);
                }
            }
            if (name.equalsIgnoreCase("request_config")) {
                seriesConfig.sendConfigTo(player);
            }
        }
        if (name.equalsIgnoreCase("set_series")) {
            if (PermissionManager.isAdmin(player) || currentSeries.getSeries() == SeriesList.UNASSIGNED) {
                SeriesList newSeries = SeriesList.getSeriesFromStringName(value);
                if (newSeries == SeriesList.UNASSIGNED) return;
                if (Main.changeSeriesTo(SeriesList.getStringNameFromSeries(newSeries))) {
                    OtherUtils.broadcastMessage(Text.literal("Successfully changed the series to " + value + ".").formatted(Formatting.GREEN));
                }
            }
        }
        if (name.equalsIgnoreCase("reset_snail_model")) {
            if (Snails.snails.containsKey(player.getUuid())) {
                Snail snail = Snails.snails.get(player.getUuid());
                snail.updateModel();
            }
            if (TriviaWildcard.snails.containsKey(player.getUuid())) {
                Snail snail = TriviaWildcard.snails.get(player.getUuid());
                snail.updateModel();
            }
        }
        if (name.equalsIgnoreCase("triple_jump")) {
            if (currentSeries.getSeries() == SeriesList.WILD_LIFE && SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
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

        int clientVersion = payload.modVersion();
        int clientCompatibility = payload.compatibility();


        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        //Check if client version is compatible with the server version
        if (clientVersion < serverCompatibility) {
            Text disconnectText = Text.literal("[Life Series Mod] Client-Server version mismatch!\n" +
                    "Update the client version to at least version "+serverCompatibilityStr);
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

        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_SERVER] Received handshake (from "+player.getNameForScoreboard()+"): {"+payload.modVersionStr()+", "+payload.modVersion()+"}");
        handshakeSuccessful.add(player.getUuid());
    }

    /*
        Sending
     */
    public static void sendTriviaPacket(ServerPlayerEntity player, String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) {
        TriviaQuestionPayload triviaQuestionPacket = new TriviaQuestionPayload(question, difficulty, timestamp, timeToComplete, answers);
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_SERVER] Sending trivia question packet to "+player.getNameForScoreboard()+"): {"+question+", " + difficulty+", " + timestamp+", " + timeToComplete + ", " + answers + "}");
        ServerPlayNetworking.send(player, triviaQuestionPacket);
    }

    public static int sendConfig(ServerPlayerEntity player, String configType, String id, int index, String name, String description, List<String> args) {
        ConfigPayload configPacket = new ConfigPayload(configType, id, index, name, description, args);
        ServerPlayNetworking.send(player, configPacket);
        return 1;
    }

    public static void sendHandshake(ServerPlayerEntity player) {
        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        HandshakePayload payload = new HandshakePayload(serverVersionStr, serverVersion, serverCompatibilityStr, serverCompatibility);
        ServerPlayNetworking.send(player, payload);
        handshakeSuccessful.remove(player.getUuid());
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_SERVER] Sending handshake to "+player.getNameForScoreboard()+": {"+serverVersionStr+", "+serverVersion+"}");
    }

    public static void sendStringPacket(ServerPlayerEntity player, String name, String value) {
        StringPayload payload = new StringPayload(name, value);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendNumberPacket(ServerPlayerEntity player, String name, double number) {
        if (player == null) return;
        NumberPayload payload = new NumberPayload(name, number);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendLongPacket(ServerPlayerEntity player, String name, long number) {
        if (player == null) return;
        LongPayload payload = new LongPayload(name, number);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendImagePacket(ServerPlayerEntity player, ImagePayload payload) {
        if (player == null) return;
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendImagePackets(ImagePayload payload) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendLongPackets(String name, long number) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            sendLongPacket(player, name, number);
        }
    }

    public static void sendUpdatePacketTo(ServerPlayerEntity player) {
        if (currentSeries instanceof WildLife) {
            sendNumberPacket(player, "hunger_version", Hunger.shuffleVersion);
            sendNumberPacket(player, "player_min_mspt", TimeDilation.MIN_PLAYER_MSPT);

            List<String> activeWildcards = new ArrayList<>();
            for (Wildcards wildcard : WildcardManager.activeWildcards.keySet()) {
                activeWildcards.add(Wildcards.getStringName(wildcard));
            }
            sendStringPacket(player, "activeWildcards", String.join("__", activeWildcards));
        }
        sendStringPacket(player, "currentSeries", SeriesList.getStringNameFromSeries(currentSeries.getSeries()));
    }

    public static void sendUpdatePackets() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            sendUpdatePacketTo(player);
        }
    }

    public static void sendPlayerDisguise(String name, String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        PlayerDisguisePayload payload = new PlayerDisguisePayload(name, hiddenUUID, hiddenName, shownUUID, shownName);
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendPlayerInvisible(UUID uuid, long timestamp) {
        LongPayload payload = new LongPayload("player_invisible__"+uuid.toString(), timestamp);
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendVignette(ServerPlayerEntity player, long durationMillis) {
        LongPayload payload = new LongPayload("show_vignette", durationMillis);
        ServerPlayNetworking.send(player, payload);
    }

    public static void tryKickFailedHandshake(ServerPlayerEntity player) {
        if (server == null) return;
        if (currentSeries.getSeries() != SeriesList.WILD_LIFE) return;
        if (wasHandshakeSuccessful(player)) return;
        Text disconnectText = Text.literal("You must have the §2Life Series mod\n§l installed on the client§r§r§f to play Wild Life!\n").append(
                Text.literal("§9§nThe Life Series mod is available on Modrinth."));
        player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
    }

    public static boolean wasHandshakeSuccessful(ServerPlayerEntity player) {
        return wasHandshakeSuccessful(player.getUuid());
    }

    public static boolean wasHandshakeSuccessful(UUID uuid) {
        return NetworkHandlerServer.handshakeSuccessful.contains(uuid);
    }
}