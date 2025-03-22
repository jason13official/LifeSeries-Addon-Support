package net.mat0u5.lifeseries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.client.ClientKeybinds;
import net.mat0u5.lifeseries.client.render.ClientRenderUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class MainClient implements ClientModInitializer {

    public static SeriesList clientCurrentSeries = SeriesList.UNASSIGNED;
    public static List<Wildcards> clientActiveWildcards = new ArrayList<>();
    public static long TIME_DILATION_TIMESTAMP = 0;
    public static long SUPERPOWER_COOLDOWN_TIMESTAMP = 0;
    public static long MIMICRY_COOLDOWN_TIMESTAMP = 0;
    public static boolean CURSE_SLIDING = false;

    public static Map<String, String> playerDisguiseNames = new HashMap<>();
    public static Map<UUID, UUID> playerDisguiseUUIDs = new HashMap<>();

    public static Map<UUID, Long> invisiblePlayers = new HashMap<>();

    public static int snailAir = 300;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(Main.MOD_ID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "lifeseries"), container, Text.translatable("Main Life Series Resourcepack"), ResourcePackActivationType.ALWAYS_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "secretlife"), container, Text.translatable("Secret Life Resourcepack"), ResourcePackActivationType.NORMAL);
        });

        NetworkHandlerClient.registerClientReceiver();
        ClientRenderUtils.onInitialize();
        ClientKeybinds.registerKeybinds();
    }

    public static boolean isRunningIntegratedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        return client.isIntegratedServerRunning();
    }

    public static boolean isClientPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        return client.player.getUuid().equals(uuid);
    }
}
