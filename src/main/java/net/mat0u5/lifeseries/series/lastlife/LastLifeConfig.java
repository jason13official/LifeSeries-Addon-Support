package net.mat0u5.lifeseries.series.lastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class LastLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "enchanting_table",
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

    public static final List<String> BLACKLISTED_BLOCKS = List.of(
            "lectern",
            "bookshelf"
    );
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

    public LastLifeConfig() {
        super("./config/"+ Main.MOD_ID,"lastlife.properties");
    }

    @Override
    public void defaultProperties() {
        defaultSessionProperties();
        getOrCreateBoolean("spawner_recipe", false);
        getOrCreateBoolean("spawn_egg_allow_on_spawner", false);
        getOrCreateInt("max_player_health", 20);
        getOrCreateInt("random_lives_min", 2);
        getOrCreateInt("random_lives_max", 6);
        getOrCreateInt("givelife_lives_max", 99);
        getOrCreateBoolean("custom_enchanter_algorithm", true);
        getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]");
        getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]");
        getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]");
    }

    @Override
    public void sendConfigTo(ServerPlayerEntity player) {

        NetworkHandlerServer.sendConfig(player, "integer", "max_player_health", 0, "Default Health", "The amount of health (half-hearts) every player will have by default.", List.of(String.valueOf(getOrCreateInt("max_player_health", 20)), "20"));

        NetworkHandlerServer.sendConfig(player, "string", "blacklist_items", 1, "Blacklisted Items", "List of banned items.", List.of(getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]"), "["+String.join(", ", BLACKLISTED_ITEMS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_blocks", 2, "Blacklisted Blocks", "List of banned blocks.", List.of(getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]"), "["+String.join(", ", BLACKLISTED_BLOCKS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_banned_enchants", 3, "Blacklisted Enchants", "List of banned enchants.", List.of(getOrCreateProperty("blacklist_banned_enchants","[]"), "[]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_clamped_enchants", 4, "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1).", List.of(getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"), "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"));


        NetworkHandlerServer.sendConfig(player, "boolean", "creative_ignore_blacklist", 5, "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists.", List.of(String.valueOf(getOrCreateBoolean("creative_ignore_blacklist", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "custom_enchanter_algorithm", 6, "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.", List.of(String.valueOf(getOrCreateBoolean("custom_enchanter_algorithm", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "double", "spawn_egg_drop_chance", 7, "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)", List.of(String.valueOf(getOrCreateDouble("spawn_egg_drop_chance", 0.05)), "0.05"));

        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_drop_only_natural", 8, "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc).", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_drop_only_natural", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_allow_on_spawner", 9, "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners.", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_allow_on_spawner", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawner_recipe", 10, "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled.", List.of(String.valueOf(getOrCreateBoolean("spawner_recipe", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "players_drop_items_on_last_death", 11, "Players Drop Items on Last Death", "Controls whether players drop their items on the last death (even if keepInventory is on).", List.of(String.valueOf(getOrCreateBoolean("players_drop_items_on_last_death", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "show_death_title_on_last_death", 12, "Show Death Title on Last Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies.", List.of(String.valueOf(getOrCreateBoolean("show_death_title_on_last_death", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "boolean", "auto_keep_inventory", 13, "Auto Keep Inventory", "Decides whether the keepInventory gamerule should be automatically turned on when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_keep_inventory", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "auto_set_worldborder", 14, "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_set_worldborder", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "mute_dead_players", 15, "Mute Dead Players", "Controls whether dead players should be allowed to type in chat or not.", List.of(String.valueOf(getOrCreateBoolean("mute_dead_players", false)), "false"));


        NetworkHandlerServer.sendConfig(player, "integer", "random_lives_min", 100, "Random Lives Min", "The minumum lives you can get from the random roll.", List.of(String.valueOf(getOrCreateInt("random_lives_min", 2)), "2"));
        NetworkHandlerServer.sendConfig(player, "integer", "random_lives_max", 101, "Random Lives Max", "The maximum lives you can get from the random roll.", List.of(String.valueOf(getOrCreateInt("random_lives_max", 6)), "6"));
        NetworkHandlerServer.sendConfig(player, "integer", "givelife_lives_max", 102, "/givelife Lives Max", "The maximum amount of lives a player can have from other players giving them lives using /givelife", List.of(String.valueOf(getOrCreateInt("givelife_lives_max", 99)), "99"));
    }
}
