package net.mat0u5.lifeseries.resources.datapack;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.nio.file.*;

public class OldDatapackManager {
    private static void deleteOldDatapacks(MinecraftServer server) {
        Path datapackFolder = server.getSavePath(WorldSavePath.DATAPACKS);
        try {
            for (SeriesList series : SeriesList.getAllImplemented()) {
                String datapackName = SeriesList.getDatapackName(series);
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

    private static void disableOldDatapacks() {
        for (SeriesList series : SeriesList.getAllImplemented()) {
            String datapackName = SeriesList.getDatapackName(series);
            OtherUtils.executeCommand("datapack disable \"file/"+datapackName+"\"");
        }
    }

    private static boolean hasOldDatapacks() {
        for (SeriesList series : SeriesList.getAllImplemented()) {
            String datapackName = SeriesList.getDatapackName(series);
            if (datapackName == null) continue;
            Path datapackPath = Paths.get("./datapacks").resolve(datapackName);
            if (Files.exists(datapackPath) && Files.isRegularFile(datapackPath)) {
                return true;
            }
        }
        return false;
    }

    public static void onServerStarted(MinecraftServer server) {
        if (!hasOldDatapacks()) return;
        disableOldDatapacks();
        TaskScheduler.scheduleTask(50, OtherUtils::reloadServerNoUpdate);
        TaskScheduler.scheduleTask(100, () -> deleteOldDatapacks(server));
    }
}
