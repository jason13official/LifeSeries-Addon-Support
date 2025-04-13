package net.mat0u5.lifeseries.series.doublelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class DoubleLifeConfig extends ConfigManager {
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

    public DoubleLifeConfig() {
        super("./config/"+ Main.MOD_ID,"doublelife.properties");
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
        getOrCreateProperty("final_death_title_subtitle", "ran out of lives!");
        getOrCreateProperty("final_death_message", "${player} ran out of lives.");
    }


    @Override
    public void sendConfigTo(ServerPlayerEntity player) {
        int index = 0;

        index += NetworkHandlerServer.sendConfig(player, "integer", "default_lives", index, "Default Lives", "The number of lives every player will have by default.", List.of(String.valueOf(getOrCreateInt("default_lives", 3)), "3"));
        index += NetworkHandlerServer.sendConfig(player, "integer", "max_player_health", index, "Default Health", "The amount of health (half-hearts) every player will have by default.", List.of(String.valueOf(getOrCreateInt("max_player_health", 20)), "20"));

        index += NetworkHandlerServer.sendConfig(player, "string", "blacklist_items", index, "Blacklisted Items", "List of banned items.", List.of(getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]"), "["+String.join(", ", BLACKLISTED_ITEMS)+"]"));
        index += NetworkHandlerServer.sendConfig(player, "string", "blacklist_blocks", index, "Blacklisted Blocks", "List of banned blocks.", List.of(getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]"), "["+String.join(", ", BLACKLISTED_BLOCKS)+"]"));
        index += NetworkHandlerServer.sendConfig(player, "string", "blacklist_banned_enchants", index, "Blacklisted Enchants", "List of banned enchants.", List.of(getOrCreateProperty("blacklist_banned_enchants","[]"), "[]"));
        index += NetworkHandlerServer.sendConfig(player, "string", "blacklist_clamped_enchants", index, "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1).", List.of(getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"), "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"));


        index += NetworkHandlerServer.sendConfig(player, "boolean", "creative_ignore_blacklist", index, "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists.", List.of(String.valueOf(getOrCreateBoolean("creative_ignore_blacklist", true)), "true"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "custom_enchanter_algorithm", index, "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.", List.of(String.valueOf(getOrCreateBoolean("custom_enchanter_algorithm", true)), "true"));

        index += NetworkHandlerServer.sendConfig(player, "double", "spawn_egg_drop_chance", index, "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)", List.of(String.valueOf(getOrCreateDouble("spawn_egg_drop_chance", 0.05)), "0.05"));

        index += NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_drop_only_natural", index, "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc).", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_drop_only_natural", true)), "true"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_allow_on_spawner", index, "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners.", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_allow_on_spawner", false)), "false"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "spawner_recipe", index, "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled.", List.of(String.valueOf(getOrCreateBoolean("spawner_recipe", false)), "false"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "players_drop_items_on_final_death", index, "Players Drop Items on Final Death", "Controls whether players drop their items on the final death (even if keepInventory is on).", List.of(String.valueOf(getOrCreateBoolean("players_drop_items_on_final_death", false)), "false"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "final_death_title_show", index, "Show Death Title on Final Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies.", List.of(String.valueOf(getOrCreateBoolean("final_death_title_show", true)), "true"));
        index += NetworkHandlerServer.sendConfig(player, "string", "final_death_title_subtitle", index, "Death Subtitle", "The subtitle that shows when a player dies (requires Show Death Title on Final Death to be set to true).", List.of(getOrCreateProperty("final_death_title_subtitle", "ran out of lives!"), "ran out of lives!"));
        index += NetworkHandlerServer.sendConfig(player, "string", "final_death_message", index, "Final Death Message", "The message that gets shown in chat when a player fully dies.", List.of(getOrCreateProperty("final_death_message", "${player} ran out of lives."), "${player} ran out of lives."));

        index += NetworkHandlerServer.sendConfig(player, "boolean", "auto_keep_inventory", index, "Keep Inventory", "Decides whether players drop their items when they die.", List.of(String.valueOf(getOrCreateBoolean("auto_keep_inventory", true)), "true"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "auto_set_worldborder", index, "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_set_worldborder", true)), "true"));
        index += NetworkHandlerServer.sendConfig(player, "boolean", "mute_dead_players", index, "Mute Dead Players", "Controls whether dead players should be allowed to type in chat or not.", List.of(String.valueOf(getOrCreateBoolean("mute_dead_players", false)), "false"));

    }
}
