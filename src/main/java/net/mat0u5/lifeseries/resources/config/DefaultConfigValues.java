package net.mat0u5.lifeseries.resources.config;

import java.util.List;

public class DefaultConfigValues {

    /*
    public final ConfigEntry<Boolean> NAME = new ConfigEntry<>(
            "key", 3, "boolean", "", ""
    );
    public final ConfigEntry<Integer> NAME = new ConfigEntry<>(
            "key", 3, "integer", "", ""
    );
    public final ConfigEntry<Double> NAME = new ConfigEntry<>(
            "key", 3, "double", "", ""
    );
    */

    public final ConfigEntry<Double> SPAWN_EGG_DROP_CHANCE = new ConfigEntry<>(
            "spawn_egg_drop_chance", 0.05, "double", "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)"
    );
    public final ConfigEntry<Boolean> SPAWN_EGG_DROP_ONLY_NATURAL = new ConfigEntry<>(
            "spawn_egg_drop_only_natural", true, "boolean", "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc)."
    );
    public final ConfigEntry<Boolean> CREATIVE_IGNORE_BLACKLIST = new ConfigEntry<>(
            "creative_ignore_blacklist", true, "boolean", "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists."
    );
    public final ConfigEntry<Boolean> AUTO_SET_WORLDBORDER = new ConfigEntry<>(
            "auto_set_worldborder", true, "boolean", "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts."
    );
    public final ConfigEntry<Boolean> KEEP_INVENTORY = new ConfigEntry<>(
            "keep_inventory", true, "boolean", "Keep Inventory", "Decides whether players drop their items when they die."
    );
    public final ConfigEntry<Boolean> PLAYERS_DROP_ITEMS_ON_FINAL_DEATH = new ConfigEntry<>(
            "players_drop_items_on_final_death", false, "boolean", "Players Drop Items on Final Death", "Controls whether players drop their items on the final death (even if keepInventory is on)."
    );
    public final ConfigEntry<Boolean> FINAL_DEATH_TITLE_SHOW = new ConfigEntry<>(
            "final_death_title_show", true, "boolean", "Show Death Title on Final Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies."
    );
    public final ConfigEntry<String> BLACKLIST_BANNED_ENCHANTS = new ConfigEntry<>(
            "blacklist_banned_enchants", "[]", "string", "Blacklisted Enchants", "List of banned enchants."
    );
    public final ConfigEntry<Boolean> MUTE_DEAD_PLAYERS = new ConfigEntry<>(
            "mute_dead_players", false, "boolean", "Mute Dead Players", "Controls whether dead players should be allowed to type in chat or not."
    );
    public final ConfigEntry<String> BLACKLIST_BANNED_POTION_EFFECTS = new ConfigEntry<>(
            "blacklist_banned_potion_effects", "[strength, instant_health, instant_damage]", "string", "Banned Potion Effects", "List of banned potion effects."
    );


    public final ConfigEntry<Boolean> SPAWNER_RECIPE = new ConfigEntry<>(
            "spawner_recipe", false, "boolean", "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled."
    );
    public final ConfigEntry<Boolean> SPAWN_EGG_ALLOW_ON_SPAWNER = new ConfigEntry<>(
            "spawn_egg_allow_on_spawner", false, "boolean", "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners."
    );
    public final ConfigEntry<Integer> MAX_PLAYER_HEALTH = new ConfigEntry<>(
            "max_player_health", 20, "integer", "Default Health", "The amount of health (half-hearts) every player will have by default."
    );
    public final ConfigEntry<Integer> DEFAULT_LIVES = new ConfigEntry<>(
            "default_lives", 3, "integer", "Default Lives", "The number of lives every player will have by default."
    );
    public final ConfigEntry<Boolean> CUSTOM_ENCHANTER_ALGORITHM = new ConfigEntry<>(
            "custom_enchanter_algorithm", false, "boolean", "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves."
    );
    public final ConfigEntry<String> BLACKLIST_ITEMS = new ConfigEntry<>(
            "blacklist_items", "[]", "string", "Blacklisted Items", "List of banned items."
    );
    public final ConfigEntry<String> BLACKLIST_BLOCKS = new ConfigEntry<>(
            "blacklist_blocks", "[]", "string", "Blacklisted Blocks", "List of banned blocks."
    );
    public final ConfigEntry<String> BLACKLIST_CLAMPED_ENCHANTS = new ConfigEntry<>(
            "blacklist_clamped_enchants", "[]", "string", "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1)."
    );
    public final ConfigEntry<String> FINAL_DEATH_TITLE_SUBTITLE = new ConfigEntry<>(
            "final_death_title_subtitle", "ran out of lives!", "string", "Death Subtitle", "The subtitle that shows when a player dies (requires Show Death Title on Final Death to be set to true)."
    );
    public final ConfigEntry<String> FINAL_DEATH_MESSAGE = new ConfigEntry<>(
            "final_death_message", "${player} ran out of lives.", "string", "Final Death Message", "The message that gets shown in chat when a player fully dies."
    );
    public final ConfigEntry<Boolean> GIVELIFE_COMMAND_ENABLED = new ConfigEntry<>(
            "givelife_command_enabled", false, "boolean", "Enable givelife command", "Controls whether the '/givelife' command is available."
    );
    public final ConfigEntry<Integer> GIVELIFE_LIVES_MAX = new ConfigEntry<>(
            "givelife_lives_max", 99, "integer", "/givelife Lives Max", "The maximum amount of lives a player can have from other players giving them lives using /givelife"
    );
    public final ConfigEntry<Boolean> TAB_LIST_SHOW_DEAD_PLAYERS = new ConfigEntry<>(
            "tab_list_show_dead_players", true, "boolean", "Tab List Show Dead Players", "Controls whether dead players show up in the tab list."
    );
    public final ConfigEntry<Boolean> TAB_LIST_SHOW_LIVES = new ConfigEntry<>(
            "tab_list_show_lives", false, "boolean", "Tab List Show Lives", "Controls whether you can see the players' lives in the tab list."
    );

    public final ConfigEntry<Boolean> LOCATOR_BAR = new ConfigEntry<>(
            "locator_bar", false, "boolean", "Locator Bar", "Enables the player Locator Bar."
    );



    public static final List<String> RELOAD_NEEDED = List.of(
            "spawner_recipe"
    );
}
