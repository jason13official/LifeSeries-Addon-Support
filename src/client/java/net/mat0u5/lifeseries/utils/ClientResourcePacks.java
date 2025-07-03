package net.mat0u5.lifeseries.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.features.SnailSkinsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;

@Environment(EnvType.CLIENT)
public class ClientResourcePacks {
    private static final String SECRET_LIFE_RESOURCEPACK = "lifeseries:secretlife";
    private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";
    public static final String SNAILS_RESOURCEPACK = "file/" + SnailSkinsClient.PACK_NAME;
    /*

    //TODO check.
    public static void applyResourcepack(UUID uuid) {
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
                if (MinecraftClient.getInstance().player.getUuid().equals(uuid)) {
                    if (currentSeason instanceof SecretLife) {
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
     */

    public static void checkClientPacks() {
        if (MainClient.clientCurrentSeason == Seasons.SECRET_LIFE) {
            enableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
        }
        else {
            disableClientResourcePack(SECRET_LIFE_RESOURCEPACK);
        }
    }

    // Enable a resource pack
    public static void enableClientResourcePack(String id) {
        enableClientResourcePack(id, false);
    }
    public static void enableClientResourcePack(String id, boolean forceReload) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() != null) {
            if (!client.getResourcePackManager().getEnabledIds().contains(id)) {
                for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
                    if (profile.getId().equals(id)) {
                        client.getResourcePackManager().enable(id);
                        Main.LOGGER.info("Enabling resourcepack " + id);
                        client.reloadResources();
                        return;
                    }
                }
            }
        }
        if (forceReload) {
            Main.LOGGER.info("Force enabling resourcepack " + id);
            client.reloadResources();
        }
    }

    // Disable a resource pack
    public static void disableClientResourcePack(String id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getResourcePackManager() != null) {
            if (client.getResourcePackManager().getEnabledIds().contains(id)) {
                for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
                    if (profile.getId().equals(id)) {
                        client.getResourcePackManager().disable(id);
                        Main.LOGGER.info("Disabling resourcepack " + id);
                        client.reloadResources();
                        return;
                    }
                }
            }
        }
    }
}
