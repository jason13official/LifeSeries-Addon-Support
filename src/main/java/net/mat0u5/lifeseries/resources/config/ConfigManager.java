package net.mat0u5.lifeseries.resources.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.aprilfools.simplelife.SimpleLifeConfig;
import net.mat0u5.lifeseries.series.doublelife.DoubleLifeConfig;
import net.mat0u5.lifeseries.series.lastlife.LastLifeConfig;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.series.secretlife.SecretLifeConfig;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.series.wildlife.WildLifeConfig;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ConfigManager extends DefaultConfigValues {

    protected Properties properties = new Properties();
    protected String folderPath;
    protected String filePath;

    protected ConfigManager(String folderPath, String filePath) {
        this.folderPath = folderPath;
        this.filePath = folderPath + "/" + filePath;
        createFileIfNotExists();
        loadProperties();
        renamedProperties();
        instantiateProperties();
    }

    protected List<ConfigEntry<?>> getDefaultConfigEntries() {
        return new ArrayList<>(List.of(
                DEFAULT_LIVES,
                MAX_PLAYER_HEALTH,
                BLACKLIST_ITEMS,
                BLACKLIST_BLOCKS,
                BLACKLIST_CLAMPED_ENCHANTS,
                BLACKLIST_BANNED_ENCHANTS,
                BLACKLIST_BANNED_POTION_EFFECTS,
                CREATIVE_IGNORE_BLACKLIST,
                CUSTOM_ENCHANTER_ALGORITHM,
                PLAYERS_DROP_ITEMS_ON_FINAL_DEATH,
                FINAL_DEATH_TITLE_SHOW,
                FINAL_DEATH_TITLE_SUBTITLE,
                FINAL_DEATH_MESSAGE,
                MUTE_DEAD_PLAYERS,
                AUTO_SET_WORLDBORDER,
                KEEP_INVENTORY,
                SPAWN_EGG_DROP_CHANCE,
                SPAWN_EGG_DROP_ONLY_NATURAL,
                SPAWN_EGG_ALLOW_ON_SPAWNER,
                SPAWNER_RECIPE,
                GIVELIFE_COMMAND_ENABLED,
                GIVELIFE_LIVES_MAX,
                TAB_LIST_SHOW_DEAD_PLAYERS,
                TAB_LIST_SHOW_LIVES

                //? if >= 1.21.6 {
                /*,LOCATOR_BAR
                *///?}
        ));
    }

    protected List<ConfigEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of());
    }

    protected List<ConfigEntry<?>> getAllConfigEntries() {
        List<ConfigEntry<?>> allEntries = new ArrayList<>();
        allEntries.addAll(getDefaultConfigEntries());
        allEntries.addAll(getSeasonSpecificConfigEntries());
        return allEntries;
    }

    protected void instantiateProperties() {
        for (ConfigEntry<?> entry : getAllConfigEntries()) {
            if (entry.defaultValue instanceof Integer integerValue) {
                getOrCreateInt(entry.key, integerValue);
            } else if (entry.defaultValue instanceof Boolean booleanValue) {
                getOrCreateBoolean(entry.key, booleanValue);
            } else if (entry.defaultValue instanceof Double doubleValue) {
                getOrCreateDouble(entry.key, doubleValue);
            } else if (entry.defaultValue instanceof String stringValue) {
                getOrCreateProperty(entry.key, stringValue);
            }
        }
    }

    public void sendConfigTo(ServerPlayerEntity player) {
        int index = 0;
        for (ConfigEntry<?> entry : getDefaultConfigEntries()) {
            String value = getPropertyAsString(entry.key, entry.defaultValue);
            index += NetworkHandlerServer.sendConfig(
                    player,
                    entry.type,
                    entry.key,
                    index,
                    entry.displayName,
                    entry.description,
                    List.of(value, entry.defaultValue.toString())
            );
        }
        index = 100;
        for (ConfigEntry<?> entry : getSeasonSpecificConfigEntries()) {
            String value = getPropertyAsString(entry.key, entry.defaultValue);
            index += NetworkHandlerServer.sendConfig(
                    player,
                    entry.type,
                    entry.key,
                    index,
                    entry.displayName,
                    entry.description,
                    List.of(value, entry.defaultValue.toString())
            );
        }
    }

    private String getPropertyAsString(String key, Object defaultValue) {
        if (defaultValue instanceof Integer intValue) {
            return String.valueOf(getOrCreateInt(key, intValue));
        } else if (defaultValue instanceof Boolean booleanValue) {
            return String.valueOf(getOrCreateBoolean(key, booleanValue));
        } else if (defaultValue instanceof Double doubleValue) {
            return String.valueOf(getOrCreateDouble(key, doubleValue));
        } else if (defaultValue instanceof String stringValue) {
            return getOrCreateProperty(key, stringValue);
        }
        return defaultValue.toString();
    }


    protected void renamedProperties() {
        renamedProperty("show_death_title_on_last_death", "final_death_title_show");
        renamedProperty("players_drop_items_on_last_death", "players_drop_items_on_final_death");
        renamedProperty("blacklist_banned_potions", "blacklist_banned_potion_effects");
        renamedProperty("auto_keep_inventory", "keep_inventory");
    }

    private void renamedProperty(String from, String to) {
        if (properties.containsKey(from)) {
            if (!properties.containsKey(to)) {
                String value = getProperty(from);
                if (value != null) {
                    setProperty(to, value);
                }
            }
            removeProperty(from);
        }
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
                    instantiateProperties();
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

    public void removeProperty(String key) {
        if (folderPath == null || filePath == null) return;
        if (!properties.containsKey(key)) return;
        properties.remove(key);
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
}
