package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.aprilfools.simplelife.SimpleLifeConfig;
import net.mat0u5.lifeseries.series.doublelife.DoubleLifeConfig;
import net.mat0u5.lifeseries.series.lastlife.LastLifeConfig;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.series.secretlife.SecretLifeConfig;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.series.wildlife.WildLifeConfig;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public abstract class ConfigManager {

    protected Properties properties = new Properties();
    protected String folderPath;
    protected String filePath;

    protected ConfigManager(String folderPath, String filePath) {
        this.folderPath = folderPath;
        this.filePath = folderPath + "/" + filePath;
        createFileIfNotExists();
        loadProperties();
        defaultProperties();
    }
    protected abstract void defaultProperties();

    protected void defaultSessionProperties() {
        getOrCreateDouble("spawn_egg_drop_chance", 0.05);
        getOrCreateBoolean("spawn_egg_drop_only_natural", true);
        getOrCreateBoolean("creative_ignore_blacklist", true);
        getOrCreateBoolean("auto_set_worldborder", true);
        getOrCreateBoolean("auto_keep_inventory", true);
        getOrCreateBoolean("players_drop_items_on_final_death", false);
        getOrCreateBoolean("final_death_title_show", true);
        getOrCreateProperty("blacklist_banned_enchants","[]");
        getOrCreateBoolean("mute_dead_players", false);
    }

    public static void moveOldMainFileIfExists() {
        File newFolder = new File("./config/lifeseries/main/");
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", newFolder);
                return;
            }
        }

        File oldFile = new File("./config/"+ Main.MOD_ID+".properties");
        if (!oldFile.exists()) return;
        File newFile = new File("./config/lifeseries/main/"+ Main.MOD_ID+".properties");
        if (newFile.exists()) {
            if (oldFile.delete()) {
                Main.LOGGER.info("Deleted old config file.");
            }
        }
        else {
            try {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Main.LOGGER.info("Moved old config file.");
            } catch (IOException e) {
                Main.LOGGER.info("Failed to move old config file.");
            }
        }
    }

    public static void createConfigs() {
        new ThirdLifeConfig();
        new LastLifeConfig();
        new DoubleLifeConfig();
        new LimitedLifeConfig();
        new SecretLifeConfig();
        new WildLifeConfig();
        new SimpleLifeConfig();
    }

    private void createFileIfNotExists() {
        if (folderPath == null || filePath == null) return;
        File configDir = new File(folderPath);
        if (!configDir.exists()) {
            if (!configDir.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", configDir);
                return;
            }
        }

        File configFile = new File(filePath);
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    Main.LOGGER.error("Failed to create file {}", configFile);
                    return;
                }
                try (OutputStream output = new FileOutputStream(configFile)) {
                    defaultProperties();
                    properties.store(output, null);
                }
            } catch (IOException ex) {
                Main.LOGGER.error(ex.getMessage());
            }
        }
    }

    public void loadProperties() {
        if (folderPath == null || filePath == null) return;

        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void setProperty(String key, String value) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void setPropertyCommented(String key, String value, String comment) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void resetProperties(String comment) {
        properties.clear();
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    /*
        Various getters
     */

    public String getProperty(String key) {
        if (folderPath == null || filePath == null) return null;
        if (properties == null) return null;

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return null;
    }

    public String getOrCreateProperty(String key, String defaultValue) {
        if (folderPath == null || filePath == null) return "";
        if (properties == null) return "";

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        setProperty(key, defaultValue);
        return defaultValue;
    }

    public boolean getOrCreateBoolean(String key, boolean defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        return defaultValue;
    }

    public double getOrCreateDouble(String key, double defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public int getOrCreateInt(String key, int defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {}
        return defaultValue;
    }

    /*
        Other
     */
    public void sendConfigTo(ServerPlayerEntity player) {
    }
}
