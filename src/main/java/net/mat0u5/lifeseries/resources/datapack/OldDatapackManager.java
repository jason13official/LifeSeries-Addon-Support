package net.mat0u5.lifeseries.resources.datapack;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Files;
import java.nio.file.Path;

public class OldDatapackManager {
    public static boolean deletedOldDatapacks = false;
    public static void deleteOldDatapacks(MinecraftServer server) {
        if (server == null) return;
        if (deletedOldDatapacks) return;
        deletedOldDatapacks = true;
        Path datapackFolder = server.getSavePath(WorldSavePath.DATAPACKS);
        try {
            for (Seasons season : Seasons.getAllImplemented()) {
                String datapackName = Seasons.getDatapackName(season);
                if (datapackName == null) continue;
                Path datapackPath = datapackFolder.resolve(datapackName);
                if (Files.exists(datapackPath) && Files.isRegularFile(datapackPath)) {
                    Files.delete(datapackPath);
                    Main.LOGGER.info("[LifeSeries] Deleted old datapack: {}", datapackName);
                }
            }
        } catch (Exception e) {
            Main.LOGGER.error("Error deleting datapacks: {}", e.getMessage());
        }
    }

    public static void disableOldDatapacks() {
        for (Seasons season : Seasons.getAllImplemented()) {
            String datapackName = Seasons.getDatapackName(season);
            OtherUtils.executeCommand("datapack disable \"file/"+datapackName+"\"");
        }
    }
}
