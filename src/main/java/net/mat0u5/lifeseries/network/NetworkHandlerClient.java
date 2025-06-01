package net.mat0u5.lifeseries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.client.ClientTaskScheduler;
import net.mat0u5.lifeseries.client.config.ClientsideConfig;
import net.mat0u5.lifeseries.client.gui.other.ConfigScreen;
import net.mat0u5.lifeseries.client.gui.other.SnailTextureInfoScreen;
import net.mat0u5.lifeseries.client.gui.series.ChooseSeriesScreen;
import net.mat0u5.lifeseries.client.gui.series.SeriesInfoScreen;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.series.SessionStatus;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.SnailSkinsClient;
import net.mat0u5.lifeseries.utils.VersionControl;
import net.mat0u5.lifeseries.client.ClientResourcePacks;
import net.mat0u5.lifeseries.client.gui.other.ChooseWildcardScreen;
import net.mat0u5.lifeseries.client.render.VignetteRenderer;
import net.mat0u5.lifeseries.network.packets.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.Trivia;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.TimeDilation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSession;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(NumberPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleNumberPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringPacket(payload.name(),payload.value()));
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
            client.execute(() -> handleLongPacket(payload.name(),payload.number()));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDisguisePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handlePlayerDisguise(payload.name(),payload.hiddenUUID(), payload.hiddenName(), payload.shownUUID(), payload.shownName()));
        });
        ClientPlayNetworking.registerGlobalReceiver(ImagePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleImagePacket(payload.name(), payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleConfigPacket(payload));
        });
    }

    public static void handleConfigPacket(ConfigPayload payload) {
        ClientsideConfig.handleConfigPacket(payload);
    }

    public static void handleImagePacket(String name, ImagePayload payload) {
        if (name.equalsIgnoreCase("snail_skin")) {
            SnailSkinsClient.handleSnailSkin(payload);
        }
    }
    
    public static void handleStringPacket(String name, String value) {
        if (name.equalsIgnoreCase("currentSeries")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current series to {}", value);
            MainClient.clientCurrentSeries = SeriesList.getSeriesFromStringName(value);
            if (Main.isClient()) {
                ClientResourcePacks.checkClientPacks();
            }
        }
        if (name.equalsIgnoreCase("sessionStatus")) {
            MainClient.clientSessionStatus = SessionStatus.getSessionName(SessionStatus.getStringName(currentSession.status));
        }
        if (name.equalsIgnoreCase("activeWildcards")) {
            List<Wildcards> newList = new ArrayList<>();
            for (String wildcardStr : value.split("__")) {
                newList.add(Wildcards.getFromString(wildcardStr));
            }
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated current wildcards to {}", newList);
            MainClient.clientActiveWildcards = newList;
        }
        if (name.equalsIgnoreCase("jump") && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.jump();
        }
        if (name.equalsIgnoreCase("reset_trivia")) {
            Trivia.resetTrivia();
        }
        if (name.equalsIgnoreCase("select_wildcards") && Main.isClient()) {
            MinecraftClient.getInstance().setScreen(new ChooseWildcardScreen());
        }
        if (name.equalsIgnoreCase("open_config")) {
            if (DependencyManager.clothConfigLoaded()) {
                ClientsideConfig.load();
                ClientTaskScheduler.scheduleTask(20, () -> {
                    MinecraftClient.getInstance().setScreen(ConfigScreen.create(MinecraftClient.getInstance().currentScreen));
                });
            }
            else if (MinecraftClient.getInstance().player != null){
                MinecraftClient.getInstance().player.sendMessage(Text.of(""), false);
                MinecraftClient.getInstance().player.sendMessage(Text.of("§cPlease install the Cloth Config mod (client-side) to modify the config from in-game."), false);
            }
        }
        if (name.equalsIgnoreCase("select_series") && Main.isClient()) {
            MinecraftClient.getInstance().setScreen(new ChooseSeriesScreen(!value.isEmpty()));
        }
        if (name.equalsIgnoreCase("series_info") && Main.isClient()) {
            SeriesList series = SeriesList.getSeriesFromStringName(value);
            if (series != SeriesList.UNASSIGNED) MinecraftClient.getInstance().setScreen(new SeriesInfoScreen(series));
        }
        if (name.equalsIgnoreCase("trivia_bot_part") && Main.isClient()) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.triviaBotPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }
        if (name.equalsIgnoreCase("snail_part") && Main.isClient()) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.snailPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }
        if (name.equalsIgnoreCase("snail_pos")) {
            try {
                String[] split = value.split("_");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                MainClient.snailPos = pos;
                MainClient.snailPosTime = System.currentTimeMillis();
            }catch(Exception e) {}
        }
        if (name.equalsIgnoreCase("trivia_snail_part") && Main.isClient()) {
            try {
                UUID uuid = UUID.fromString(value);
                MainClient.triviaSnailPartUUIDs.add(uuid);
            }catch(Exception e) {}
        }
        if (name.equalsIgnoreCase("trivia_snail_pos")) {
            try {
                String[] split = value.split("_");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                MainClient.triviaSnailPos = pos;
                MainClient.triviaSnailPosTime = System.currentTimeMillis();
            }catch(Exception e) {}
        }
        if (name.equalsIgnoreCase("snail_textures_info") && Main.isClient()) {
            MinecraftClient.getInstance().setScreen(new SnailTextureInfoScreen());
        }
        if (name.equalsIgnoreCase("prevent_gliding") && Main.isClient()) {
            MainClient.preventGliding = value.equalsIgnoreCase("true");
        }
    }

    public static void handleNumberPacket(String name, double number) {
        int intNumber = (int) number;
        if (name.equalsIgnoreCase("player_min_mspt")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Updated min. player MSPT to {}", number);
            TimeDilation.MIN_PLAYER_MSPT = (float) number;
        }
        if (name.equalsIgnoreCase("snail_air")) {
            MainClient.snailAir = intNumber;
        }
        if (name.equalsIgnoreCase("fake_thunder") && MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.setLightningTicksLeft(intNumber);
        }
        if (name.equalsIgnoreCase("mute")) {
            MainClient.mutedForTicks = intNumber;
        }
    }

    public static void handleLongPacket(String name, long number) {
        if (name.equalsIgnoreCase("superpower_cooldown")) {
            MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP = number;
        }
        if (name.equalsIgnoreCase("show_vignette")) {
            if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Showing vignette for {}", number);
            VignetteRenderer.showVignetteFor(0.35f, number);
        }
        if (name.equalsIgnoreCase("mimicry_cooldown")) {
            MainClient.MIMICRY_COOLDOWN_TIMESTAMP = number;
        }
        if (name.startsWith("player_invisible__")) {
            try {
                UUID uuid = UUID.fromString(name.replaceFirst("player_invisible__",""));
                if (number == 0) {
                    MainClient.invisiblePlayers.remove(uuid);
                }
                else {
                    MainClient.invisiblePlayers.put(uuid, number);
                }
            }catch(Exception ignored) {}
        }

        if (name.equalsIgnoreCase("time_dilation")) {
            MainClient.TIME_DILATION_TIMESTAMP = number;
        }

        if (name.equalsIgnoreCase("session_timer")) {
            MainClient.sessionTime = number;
            MainClient.sessionTimeLastUpdated = System.currentTimeMillis();
        }

        if (name.startsWith("limited_life_timer__")) {
            MainClient.limitedLifeTimerColor = name.replaceFirst("limited_life_timer__","");
            long currentTime = System.currentTimeMillis();
            long runOutOfLivesAt = currentTime + number * 1000;
            if (Math.abs(MainClient.limitedLifeTime-runOutOfLivesAt) > 2000) {
                MainClient.limitedLifeTime = runOutOfLivesAt;
                MainClient.limitedLifeTimeLastActuallyUpdated = currentTime;
            }
            MainClient.limitedLifeTimeLastUpdated = currentTime;
        }

        if (name.equalsIgnoreCase("curse_sliding")) {
            MainClient.CURSE_SLIDING = number;
        }
    }

    public static void handlePlayerDisguise(String name, String hiddenUUID, String hiddenName, String shownUUID, String shownName) {
        if (name.equalsIgnoreCase("player_disguise")) {
            if (shownName.isEmpty()) {
                MainClient.playerDisguiseNames.remove(hiddenName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    MainClient.playerDisguiseUUIDs.remove(hideUUID);
                }catch(Exception ignored) {}
            }
            else {
                MainClient.playerDisguiseNames.put(hiddenName, shownName);
                try {
                    UUID hideUUID = UUID.fromString(hiddenUUID);
                    UUID showUUID = UUID.fromString(shownUUID);
                    MainClient.playerDisguiseUUIDs.put(hideUUID, showUUID);
                }catch(Exception ignored) {}
            }

        }
    }

    public static void handleHandshake(HandshakePayload payload) {
        String clientVersionStr = Main.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCmpatibilityMin();

        int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
        int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

        HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion, clientCompatibilityStr, clientCompatibility);
        ClientPlayNetworking.send(sendPayload);
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sent handshake");
    }

    /*
        Sending
     */

    public static void sendConfigUpdate(String configType, String id, int index, String name, String description, List<String> args) {
        ConfigPayload configPacket = new ConfigPayload(configType, id, index, name, description, args);
        ClientPlayNetworking.send(configPacket);
    }

    public static void sendTriviaAnswer(int answer) {
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sending trivia answer: {}", answer);
        ClientPlayNetworking.send(new NumberPayload("trivia_answer", answer));
    }

    public static void sendHoldingJumpPacket() {
        ClientPlayNetworking.send(new StringPayload("holding_jump", "true"));
    }

    public static void pressSuperpowerKey() {
        ClientPlayNetworking.send(new StringPayload("superpower_key", "true"));
    }

    public static void sendStringPacket(String name, String value) {
        ClientPlayNetworking.send(new StringPayload(name, value));
    }
}
