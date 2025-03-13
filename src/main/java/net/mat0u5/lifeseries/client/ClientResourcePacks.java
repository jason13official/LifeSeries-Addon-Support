package net.mat0u5.lifeseries.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.secretlife.SecretLife;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.SnailSkinsClient;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Environment(EnvType.CLIENT)
public class ClientResourcePacks {
    private static final String SECRET_LIFE_RESOURCEPACK = "lifeseries:secretlife";
    public static final String SNAILS_RESOURCEPACK = "file/" + SnailSkinsClient.PACK_NAME;
    public static void applyResourcepack(UUID uuid) {
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
                if (MinecraftClient.getInstance().player.getUuid().equals(uuid)) {
                    if (currentSeries instanceof SecretLife) {
                        enableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
                    }
                    else {
                        disableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
                    }
                }
                else {
                    PlayerUtils.applyServerResourcepack(uuid);
                }
            }

    }

    public static void checkClientPacks() {
        if (Main.isClient() && !Main.isLogicalSide() && MainClient.clientCurrentSeries == SeriesList.SECRET_LIFE) {
            enableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
        }
        else {
            disableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
        }
    }

    // Enable a resource pack
    public static void enableClientResourcePack(String id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() != null) {
            if (client.getResourcePackManager().getEnabledIds().contains(id)) return;
            for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
                if (profile.getId().equals(id)) {
                    client.getResourcePackManager().enable(id);
                    client.reloadResources();
                    return;
                }
            }
        }
    }

    // Disable a resource pack
    public static void disableClientResourcePack(String id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() != null) {
            if (!client.getResourcePackManager().getEnabledIds().contains(id)) return;
            for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
                if (profile.getId().equals(id)) {
                    client.getResourcePackManager().disable(id);
                    client.reloadResources();
                    return;
                }
            }
        }
    }

    public static boolean isRunningIntegratedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        return client.isIntegratedServerRunning();
    }
}
