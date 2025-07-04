package net.mat0u5.lifeseries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.registries.ClientRegistries;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.interfaces.IClientHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class MainClient implements ClientModInitializer, IClientHelper {

    public static Seasons clientCurrentSeason = Seasons.UNASSIGNED;
    public static SessionStatus clientSessionStatus = SessionStatus.NOT_STARTED;
    public static List<Wildcards> clientActiveWildcards = new ArrayList<>();
    public static long TIME_DILATION_TIMESTAMP = 0;
    public static long SUPERPOWER_COOLDOWN_TIMESTAMP = 0;
    public static long MIMICRY_COOLDOWN_TIMESTAMP = 0;
    public static long CURSE_SLIDING = 0;

    public static Map<String, String> playerDisguiseNames = new HashMap<>();
    public static Map<UUID, UUID> playerDisguiseUUIDs = new HashMap<>();
    public static Map<UUID, Long> invisiblePlayers = new HashMap<>();
    public static List<UUID> triviaBotPartUUIDs = new ArrayList<>();
    public static List<UUID> snailPartUUIDs = new ArrayList<>();
    public static BlockPos snailPos = null;
    public static long snailPosTime = 0;
    public static List<UUID> triviaSnailPartUUIDs = new ArrayList<>();
    public static BlockPos triviaSnailPos = null;
    public static long triviaSnailPosTime = 0;
    public static int snailAir = 300;
    public static long snailAirTimestamp = 0;
    public static boolean preventGliding = false;
    public static int mutedForTicks = 0;
    public static long sessionTime = 0;
    public static long sessionTimeLastUpdated = 0;

    public static String limitedLifeTimerColor = "";
    public static long limitedLifeTimeLastUpdated = 0;
    public static long limitedLifeLives = 0;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(Main.MOD_ID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "lifeseries"), container, Text.of("Main Life Series Resourcepack"), ResourcePackActivationType.ALWAYS_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "secretlife"), container, Text.of("Secret Life Resourcepack"), ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Main.MOD_ID, "minimal_armor"), container, Text.of("Minimal Armor Resourcepack"), ResourcePackActivationType.ALWAYS_ENABLED);
        });

        NetworkHandlerClient.registerClientReceiver();
        ClientRenderer.onInitialize();
        ClientRegistries.registerModStuff();
        Main.setClientHelper(this);
    }

    public static boolean isClientPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        return client.player.getUuid().equals(uuid);
    }

    @Override
    public boolean isRunningIntegratedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        return client.isIntegratedServerRunning();
    }

    @Override
    public boolean isMainClientPlayer(UUID uuid) {
        return isClientPlayer(uuid);
    }

    @Override
    public Seasons getCurrentSeason() {
        return clientCurrentSeason;
    }

    @Override
    public List<Wildcards> getActiveWildcards() {
        return clientActiveWildcards;
    }
}
