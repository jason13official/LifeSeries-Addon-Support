package net.mat0u5.lifeseries.series.thirdlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ThirdLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
        "mace",
        "end_crystal",
        "leather_helmet",
        "chainmail_helmet",
        "golden_helmet",
        "iron_helmet",
        "diamond_helmet",
        "netherite_helmet",
        "turtle_helmet",
        "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = new ArrayList<>();
    public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
            "sharpness",
            "smite",
            "bane_of_arthropods",
            "fire_aspect",
            "knockback",
            "sweeping_edge",

            "power",
            "punch",

            "protection",
            "projectile_protection",
            "blast_protection",
            "fire_protection",
            "feather_falling",
            "thorns",

            "breach",
            "density",
            "wind_burst",

            "multishot",
            "piercing",
            "quick_charge"
    );

    public ThirdLifeConfig() {
        super("./config/"+ Main.MOD_ID,"thirdlife.properties");
    }

    @Override
    public void defaultProperties() {
        defaultSessionProperties();
        getOrCreateBoolean("spawner_recipe", false);
        getOrCreateBoolean("spawn_egg_allow_on_spawner", false);
        getOrCreateInt("max_player_health", 20);
        getOrCreateInt("default_lives", 3);
        getOrCreateBoolean("custom_enchanter_algorithm", true);
        getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]");
        getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]");
        getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]");
    }
    /*
    {
        NetworkHandlerServer.sendConfig(player, "integer", "default_lives", 0, "Default Lives", "The number of lives every player will have by default.", List.of(String.valueOf(getOrCreateInt("default_lives", 3)), "3"));
        NetworkHandlerServer.sendConfig(player, "integer", "max_player_health", 1, "Default Health", "The amount of health (half-hearts) every player will have by default.", List.of(String.valueOf(getOrCreateInt("max_player_health", 20)), "20"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_items", 2, "Blacklisted Items", "List of banned items.", List.of(getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]"), "["+String.join(", ", BLACKLISTED_ITEMS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_blocks", 3, "Blacklisted Blocks", "List of banned blocks.", List.of(getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]"), "["+String.join(", ", BLACKLISTED_BLOCKS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_clamped_enchants", 5, "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1).", List.of(getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"), "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"));
        NetworkHandlerServer.sendConfig(player, "boolean", "custom_enchanter_algorithm", 7, "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.", List.of(String.valueOf(getOrCreateBoolean("custom_enchanter_algorithm", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_allow_on_spawner", 10, "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners.", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_allow_on_spawner", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawner_recipe", 11, "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled.", List.of(String.valueOf(getOrCreateBoolean("spawner_recipe", false)), "false"));

    }
    */
    @Override
    public void sendConfigTo(ServerPlayerEntity player) {

        NetworkHandlerServer.sendConfig(player, "integer", "default_lives", 0, "Default Lives", "The number of lives every player will have by default.", List.of(String.valueOf(getOrCreateInt("default_lives", 3)), "3"));
        NetworkHandlerServer.sendConfig(player, "integer", "max_player_health", 1, "Default Health", "The amount of health (half-hearts) every player will have by default.", List.of(String.valueOf(getOrCreateInt("max_player_health", 20)), "20"));

        NetworkHandlerServer.sendConfig(player, "string", "blacklist_items", 2, "Blacklisted Items", "List of banned items.", List.of(getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]"), "["+String.join(", ", BLACKLISTED_ITEMS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_blocks", 3, "Blacklisted Blocks", "List of banned blocks.", List.of(getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]"), "["+String.join(", ", BLACKLISTED_BLOCKS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_banned_enchants", 4, "Blacklisted Enchants", "List of banned enchants.", List.of(getOrCreateProperty("blacklist_banned_enchants","[]"), "[]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_clamped_enchants", 5, "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1).", List.of(getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"), "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"));


        NetworkHandlerServer.sendConfig(player, "boolean", "creative_ignore_blacklist", 6, "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists.", List.of(String.valueOf(getOrCreateBoolean("creative_ignore_blacklist", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "custom_enchanter_algorithm", 7, "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.", List.of(String.valueOf(getOrCreateBoolean("custom_enchanter_algorithm", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "double", "spawn_egg_drop_chance", 8, "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)", List.of(String.valueOf(getOrCreateDouble("spawn_egg_drop_chance", 0.05)), "0.05"));

        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_drop_only_natural", 9, "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc).", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_drop_only_natural", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_allow_on_spawner", 10, "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners.", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_allow_on_spawner", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawner_recipe", 11, "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled.", List.of(String.valueOf(getOrCreateBoolean("spawner_recipe", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "players_drop_items_on_last_death", 12, "Players Drop Items on Last Death", "Controls whether players drop their items on the last death (even if keepInventory is on).", List.of(String.valueOf(getOrCreateBoolean("players_drop_items_on_last_death", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "show_death_title_on_last_death", 13, "Show Death Title on Last Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies.", List.of(String.valueOf(getOrCreateBoolean("show_death_title_on_last_death", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "boolean", "auto_keep_inventory", 14, "Auto Keep Inventory", "Decides whether the keepInventory gamerule should be automatically turned on when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_keep_inventory", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "auto_set_worldborder", 15, "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_set_worldborder", true)), "true"));

    }
}
