package com.cursee.ls_addon_support.utils;

import static com.cursee.ls_addon_support.LSAddonSupportClient.clientConfig;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ClientConfig;
import com.cursee.ls_addon_support.features.SnailSkinsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;

public class ClientResourcePacks {

  public static final String SNAILS_RESOURCEPACK = "file/" + SnailSkinsClient.PACK_NAME;
  private static final String MINIMAL_ARMOR_RESOURCEPACK = "lifeseries:minimal_armor";

  public static void checkClientPacks() {
    handleClientResourcepack(MINIMAL_ARMOR_RESOURCEPACK,
        ClientConfig.MINIMAL_ARMOR.get(clientConfig));
  }

  public static void handleClientResourcepack(String id, boolean action) {
    if (action) {
      enableClientResourcePack(id);
    } else {
      disableClientResourcePack(id);
    }
  }

  public static void enableClientResourcePack(String id) {
    enableClientResourcePack(id, false);
  }

  public static void enableClientResourcePack(String id, boolean forceReload) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.getResourcePackManager() != null && !client.getResourcePackManager().getEnabledIds()
        .contains(id)) {
      for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
        if (profile.getId().equals(id)) {
          client.getResourcePackManager().enable(id);
          LSAddonSupport.LOGGER.info("Enabling resourcepack " + id);
          client.reloadResources();
          return;
        }
      }
    }
    if (forceReload) {
      LSAddonSupport.LOGGER.info("Force enabling resourcepack " + id);
      client.reloadResources();
    }
  }

  public static void disableClientResourcePack(String id) {
    MinecraftClient client = MinecraftClient.getInstance();
      if (client.getResourcePackManager() == null) {
          return;
      }
      if (!client.getResourcePackManager().getEnabledIds().contains(id)) {
          return;
      }

    for (ResourcePackProfile profile : client.getResourcePackManager().getProfiles()) {
      if (profile.getId().equals(id)) {
        client.getResourcePackManager().disable(id);
        LSAddonSupport.LOGGER.info("Disabling resourcepack " + id);
        client.reloadResources();
        return;
      }
    }
  }
}
