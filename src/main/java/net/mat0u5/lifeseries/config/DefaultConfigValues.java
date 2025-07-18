package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.List;

public class DefaultConfigValues {

    /*
    public final ConfigFileEntry<Boolean> NAME = new ConfigFileEntry<>(
            "key", false, "", "", ""
    );
    public final ConfigFileEntry<Integer> NAME = new ConfigFileEntry<>(
            "key", 3, "", "", ""
    );
    public final ConfigFileEntry<Double> NAME = new ConfigFileEntry<>(
            "key", 3.0, "", "", ""
    );
    */

    public final ConfigFileEntry<Double> SPAWN_EGG_DROP_CHANCE = new ConfigFileEntry<>(
            "spawn_egg_drop_chance", 0.05, ConfigTypes.PERCENTAGE, "global.spawnegg", "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)"
    );
    public final ConfigFileEntry<Boolean> SPAWN_EGG_DROP_ONLY_NATURAL = new ConfigFileEntry<>(
            "spawn_egg_drop_only_natural", true, "global.spawnegg", "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc)."
    );
    public final ConfigFileEntry<Boolean> CREATIVE_IGNORE_BLACKLIST = new ConfigFileEntry<>(
            "creative_ignore_blacklist", true, "global.blacklist", "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists."
    );
    public final ConfigFileEntry<Boolean> AUTO_SET_WORLDBORDER = new ConfigFileEntry<>(
            "auto_set_worldborder", true, "global", "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts."
    );
    public final ConfigFileEntry<Boolean> KEEP_INVENTORY = new ConfigFileEntry<>(
            "keep_inventory", true, "global", "Keep Inventory", "Decides whether players drop their items when they die."
    );
    public final ConfigFileEntry<Boolean> PLAYERS_DROP_ITEMS_ON_FINAL_DEATH = new ConfigFileEntry<>(
            "players_drop_items_on_final_death", false, "global.finaldeath", "Players Drop Items on Final Death", "Controls whether players drop their items on the final death (even if keepInventory is on)."
    );
    public final ConfigFileEntry<Boolean> FINAL_DEATH_TITLE_SHOW = new ConfigFileEntry<>(
            "final_death_title_show", true, "global.finaldeath", "Show Death Title on Final Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies."
    );
    public final ConfigFileEntry<String> BLACKLIST_BANNED_ENCHANTS = new ConfigFileEntry<>(
            "blacklist_banned_enchants", "[]", ConfigTypes.ENCHANT_LIST, "global.blacklist", "Blacklisted Enchants", "List of banned enchants."
    );
    public final ConfigFileEntry<Boolean> MUTE_DEAD_PLAYERS = new ConfigFileEntry<>(
            "mute_dead_players", false, "global", "Mute Dead Players", "Controls whether dead players should be allowed to type in chat or not."
    );
    public final ConfigFileEntry<String> BLACKLIST_BANNED_POTION_EFFECTS = new ConfigFileEntry<>(
            "blacklist_banned_potion_effects", "[strength, instant_health, instant_damage]", ConfigTypes.EFFECT_LIST, "global.blacklist", "Banned Potion Effects", "List of banned potion effects."
    );
    public final ConfigFileEntry<Boolean> SPAWNER_RECIPE = new ConfigFileEntry<>(
            "spawner_recipe", false, "global.spawnegg", "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled."
    );
    public final ConfigFileEntry<Boolean> SPAWN_EGG_ALLOW_ON_SPAWNER = new ConfigFileEntry<>(
            "spawn_egg_allow_on_spawner", false, "global.spawnegg", "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners."
    );
    public final ConfigFileEntry<Integer> MAX_PLAYER_HEALTH = new ConfigFileEntry<>(
            "max_player_health", 20, ConfigTypes.HEARTS, "global", "Default Health", "The amount of health (half-hearts) every player will have by default."
    );
    public final ConfigFileEntry<Integer> DEFAULT_LIVES = new ConfigFileEntry<>(
            "default_lives", 3, "global", "Default Lives", "The number of lives every player will have by default."
    );
    public final ConfigFileEntry<Boolean> CUSTOM_ENCHANTER_ALGORITHM = new ConfigFileEntry<>(
            "custom_enchanter_algorithm", false, "global", "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves."
    );
    public final ConfigFileEntry<String> BLACKLIST_ITEMS = new ConfigFileEntry<>(
            "blacklist_items", "[]", ConfigTypes.ITEM_LIST, "global.blacklist", "Blacklisted Items", "List of banned items."
    );
    public final ConfigFileEntry<String> BLACKLIST_BLOCKS = new ConfigFileEntry<>(
            "blacklist_blocks", "[]", ConfigTypes.BLOCK_LIST, "global.blacklist", "Blacklisted Blocks", "List of banned blocks."
    );
    public final ConfigFileEntry<String> BLACKLIST_CLAMPED_ENCHANTS = new ConfigFileEntry<>(
            "blacklist_clamped_enchants", "[]", ConfigTypes.ENCHANT_LIST, "global.blacklist", "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1)."
    );
    public final ConfigFileEntry<String> FINAL_DEATH_TITLE_SUBTITLE = new ConfigFileEntry<>(
            "final_death_title_subtitle", "ran out of lives!", "global.finaldeath", "Death Subtitle", "The subtitle that shows when a player dies (requires Show Death Title on Final Death to be set to true)."
    );
    public final ConfigFileEntry<String> FINAL_DEATH_MESSAGE = new ConfigFileEntry<>(
            "final_death_message", "${player} ran out of lives.", "global.finaldeath", "Final Death Message", "The message that gets shown in chat when a player fully dies."
    );
    public final ConfigFileEntry<Boolean> GIVELIFE_COMMAND_ENABLED = new ConfigFileEntry<>(
            "givelife_command_enabled", false, "{global.givelife}", "Enable givelife command", "Controls whether the '/givelife' command is available."
    );
    public final ConfigFileEntry<Integer> GIVELIFE_LIVES_MAX = new ConfigFileEntry<>(
            "givelife_lives_max", 99, "global.givelife", "/givelife Lives Max", "The maximum amount of lives a player can have from other players giving them lives using /givelife"
    );
    public final ConfigFileEntry<Boolean> TAB_LIST_SHOW_DEAD_PLAYERS = new ConfigFileEntry<>(
            "tab_list_show_dead_players", true, "global.tablist", "Tab List Show Dead Players", "Controls whether dead players show up in the tab list."
    );
    public final ConfigFileEntry<Boolean> TAB_LIST_SHOW_LIVES = new ConfigFileEntry<>(
            "tab_list_show_lives", false, "global.tablist", "Tab List Show Lives", "Controls whether you can see the players' lives in the tab list."
    );

    public final ConfigFileEntry<Boolean> LOCATOR_BAR = new ConfigFileEntry<>(
            "locator_bar", false, "global", "Locator Bar", "Enables the player Locator Bar."
    );



    /*
     * Group Entries
     */
    public final ConfigFileEntry<Object> GROUP_GLOBAL = new ConfigFileEntry<>(
            "group_global", null, ConfigTypes.TEXT, "{global}[no_sidebar, closed]", "General Settings", ""
    );
    public final ConfigFileEntry<Object> GROUP_SEASON = new ConfigFileEntry<>(
            "group_season", null, ConfigTypes.TEXT, "{season}[no_sidebar, closed]", "Season Specific Settings", ""
    );
    public final ConfigFileEntry<Object> GROUP_TABLIST = new ConfigFileEntry<>(
            "group_tablist", null, ConfigTypes.TEXT, "{global.tablist}", "Tab List", ""
    );
    public final ConfigFileEntry<Object> GROUP_BLACKLIST = new ConfigFileEntry<>(
            "group_blacklist", null, ConfigTypes.TEXT, "{global.blacklist}", "Blacklists", ""
    );
    public final ConfigFileEntry<Object> GROUP_FINAL_DEATH = new ConfigFileEntry<>(
            "group_final_death", null, ConfigTypes.TEXT, "{global.finaldeath}", "Final Death", ""
    );
    public final ConfigFileEntry<Object> GROUP_SPAWN_EGG = new ConfigFileEntry<>(
            "group_spawn_egg", null, ConfigTypes.TEXT, "{global.spawnegg}", "Spawn Egg", ""
    );

    public static final List<String> RELOAD_NEEDED = List.of(
            "spawner_recipe"
    );
}
