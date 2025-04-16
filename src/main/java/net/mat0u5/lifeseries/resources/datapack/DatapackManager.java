package net.mat0u5.lifeseries.resources.datapack;

import net.minecraft.server.MinecraftServer;

public class DatapackManager {
    public static void onServerStarted(MinecraftServer server) {
        OldDatapackManager.onServerStarted(server);
    }
}
