package net.mat0u5.lifeseries.series.wildlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class WildLifeConfig extends ConfigManager {
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

    public WildLifeConfig() {
        super("./config/"+ Main.MOD_ID,"wildlife.properties");
    }

    @Override
    public void defaultProperties() {
        defaultSessionProperties();
        getOrCreateInt("wildcard_hunger_randomize_interval", 36000);

        getOrCreateDouble("wildcard_sizeshifting_min_size", 0.25);
        getOrCreateDouble("wildcard_sizeshifting_max_size", 3);
        getOrCreateDouble("wildcard_sizeshifting_size_change_multiplier", 1);
        //getOrCreateBoolean("wildcard_sizeshifting_prevent_shift_falling", true);

        getOrCreateDouble("wildcard_timedilation_max_player_tps", 40);

        getOrCreateDouble("wildcard_snails_speed_multiplier", 1);
        getOrCreateBoolean("wildcard_snails_drown_players", true);

        getOrCreateInt("wildcard_mobswap_start_spawn_delay", 7200);
        getOrCreateInt("wildcard_mobswap_end_spawn_delay", 2400);
        getOrCreateInt("wildcard_mobswap_spawn_mobs", 250);
        getOrCreateDouble("wildcard_mobswap_boss_chance_multiplier", 1);

        getOrCreateBoolean("wildcard_trivia_bots_can_enter_boats", true);
        getOrCreateInt("wildcard_trivia_bots_per_player", 5);
        getOrCreateInt("wildcard_trivia_seconds_easy", 180);
        getOrCreateInt("wildcard_trivia_seconds_normal", 240);
        getOrCreateInt("wildcard_trivia_seconds_hard", 300);

        getOrCreateInt("wildcard_superpowers_windcharge_max_mace_damage", 2);

        getOrCreateBoolean("spawner_recipe", true);
        getOrCreateBoolean("spawn_egg_allow_on_spawner", true);
        getOrCreateInt("max_player_health", 20);
        getOrCreateInt("default_lives", 6);
        getOrCreateBoolean("custom_enchanter_algorithm", true);
        getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]");
        getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]");
        getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]");


        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");
    }

    @Override
    public void sendConfigTo(ServerPlayerEntity player) {

        NetworkHandlerServer.sendConfig(player, "integer", "default_lives", 0, "Default Lives", "The number of lives every player will have by default.", List.of(String.valueOf(getOrCreateInt("default_lives", 6)), "6"));
        NetworkHandlerServer.sendConfig(player, "integer", "max_player_health", 1, "Default Health", "The amount of health (half-hearts) every player will have by default.", List.of(String.valueOf(getOrCreateInt("max_player_health", 20)), "20"));

        NetworkHandlerServer.sendConfig(player, "string", "blacklist_items", 2, "Blacklisted Items", "List of banned items.", List.of(getOrCreateProperty("blacklist_items","["+String.join(", ", BLACKLISTED_ITEMS)+"]"), "["+String.join(", ", BLACKLISTED_ITEMS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_blocks", 3, "Blacklisted Blocks", "List of banned blocks.", List.of(getOrCreateProperty("blacklist_blocks","["+String.join(", ", BLACKLISTED_BLOCKS)+"]"), "["+String.join(", ", BLACKLISTED_BLOCKS)+"]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_banned_enchants", 4, "Blacklisted Enchants", "List of banned enchants.", List.of(getOrCreateProperty("blacklist_banned_enchants","[]"), "[]"));
        NetworkHandlerServer.sendConfig(player, "string", "blacklist_clamped_enchants", 5, "Clamped Enchants", "List of enchantments clamped to level 1 (any higher levels will be set to lvl1).", List.of(getOrCreateProperty("blacklist_clamped_enchants","["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"), "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]"));


        NetworkHandlerServer.sendConfig(player, "boolean", "creative_ignore_blacklist", 6, "Creative Ignore Blacklist", "Controls whether players in creative mode are able to bypass the blacklists.", List.of(String.valueOf(getOrCreateBoolean("creative_ignore_blacklist", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "custom_enchanter_algorithm", 7, "Custom Enchanter Algorithm", "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.", List.of(String.valueOf(getOrCreateBoolean("custom_enchanter_algorithm", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "double", "spawn_egg_drop_chance", 8, "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)", List.of(String.valueOf(getOrCreateDouble("spawn_egg_drop_chance", 0.05)), "0.05"));

        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_drop_only_natural", 9, "Spawn Egg Only Natural Drops", "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc).", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_drop_only_natural", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawn_egg_allow_on_spawner", 10, "Spawn Egg Allow on Spawners", "Controls whether players should be able to use the spawn eggs on spawners.", List.of(String.valueOf(getOrCreateBoolean("spawn_egg_allow_on_spawner", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "spawner_recipe", 11, "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled.", List.of(String.valueOf(getOrCreateBoolean("spawner_recipe", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "players_drop_items_on_last_death", 12, "Players Drop Items on Last Death", "Controls whether players drop their items on the last death (even if keepInventory is on).", List.of(String.valueOf(getOrCreateBoolean("players_drop_items_on_last_death", false)), "false"));
        NetworkHandlerServer.sendConfig(player, "boolean", "show_death_title_on_last_death", 13, "Show Death Title on Last Death", "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies.", List.of(String.valueOf(getOrCreateBoolean("show_death_title_on_last_death", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "boolean", "auto_keep_inventory", 14, "Auto Keep Inventory", "Decides whether the keepInventory gamerule should be automatically turned on when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_keep_inventory", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "auto_set_worldborder", 15, "Auto Set Worldborder", "Decides whether the world border should be shrunk when the server starts.", List.of(String.valueOf(getOrCreateBoolean("auto_set_worldborder", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "boolean", "mute_dead_players", 16, "Mute Dead Players", "Controls whether dead players should be allowed to type in chat or not.", List.of(String.valueOf(getOrCreateBoolean("mute_dead_players", false)), "false"));



        NetworkHandlerServer.sendConfig(player, "double", "wildcard_sizeshifting_min_size", 100, "Size Shifting: Min Size", "Smallest size you can achieve during Size Shifting.", List.of(String.valueOf(getOrCreateDouble("wildcard_sizeshifting_min_size", 0.25)), "0.25"));
        NetworkHandlerServer.sendConfig(player, "double", "wildcard_sizeshifting_max_size", 101, "Size Shifting: Max Size", "Biggest size you can achieve during Size Shifting.", List.of(String.valueOf(getOrCreateDouble("wildcard_sizeshifting_max_size", 3)), "3"));
        NetworkHandlerServer.sendConfig(player, "double", "wildcard_sizeshifting_size_change_multiplier", 102, "Size Shifting: Change Multiplier", "The speed with which you change your size during Size Shifting.", List.of(String.valueOf(getOrCreateDouble("wildcard_sizeshifting_size_change_multiplier", 1)), "1"));
        //NetworkHandlerServer.sendConfig(player, "boolean", "wildcard_sizeshifting_prevent_shift_falling", 103,"Size Shifting: Prevent Shift Falling", "Prevent players from falling from blocks when shifting.", List.of(String.valueOf(getOrCreateBoolean("wildcard_sizeshifting_prevent_shift_falling", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_hunger_randomize_interval", 103, "Hunger: Randomize Interval", "The duration between food changes, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_hunger_randomize_interval", 36000)), "36000"));

        NetworkHandlerServer.sendConfig(player, "double", "wildcard_snails_speed_multiplier", 104, "Snails: Speed Multiplier", "Snail movement speed multiplier.", List.of(String.valueOf(getOrCreateDouble("wildcard_snails_speed_multiplier", 1)), "1"));
        NetworkHandlerServer.sendConfig(player, "boolean", "wildcard_snails_drown_players", 105, "Snails: Drown Players", "Controls whether snails can drown players when the snails are underwater.", List.of(String.valueOf(getOrCreateBoolean("wildcard_snails_drown_players", true)), "true"));

        NetworkHandlerServer.sendConfig(player, "double", "wildcard_timedilation_max_player_tps", 106, "Time Dilation: Max Player TPS", "Controls the maximum speed the PLAYERS themselves can move (not the world).", List.of(String.valueOf(getOrCreateDouble("wildcard_timedilation_max_player_tps", 40)), "40"));

        NetworkHandlerServer.sendConfig(player, "boolean", "wildcard_trivia_bots_can_enter_boats", 107, "Trivia: Bots Can Enter Boats", "Controls whether trivia bots can enter boats.", List.of(String.valueOf(getOrCreateBoolean("wildcard_trivia_bots_can_enter_boats", true)), "true"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_trivia_bots_per_player", 108, "Trivia: Bots per Player", "The amount of trivia bots that will spawn for each player over the session.", List.of(String.valueOf(getOrCreateInt("wildcard_trivia_bots_per_player", 5)), "5"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_trivia_seconds_easy", 109, "Trivia: Easy Timer", "Easy question timer length, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_trivia_seconds_easy", 180)), "180"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_trivia_seconds_normal", 110, "Trivia: Normal Timer", "Normal question timer length, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_trivia_seconds_normal", 240)), "240"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_trivia_seconds_hard", 111, "Trivia: Hard Timer", "Hard question timer length, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_trivia_seconds_hard", 300)), "300"));

        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_mobswap_start_spawn_delay", 112, "Mob Swap: Session Start Spawn Delay", "The delay between mob spawns at the START of the session, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_mobswap_start_spawn_delay", 7200)), "7200"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_mobswap_end_spawn_delay", 113, "Mob Swap: Session End Spawn Delay", "The delay between mob spawns at the END of the session, in seconds.", List.of(String.valueOf(getOrCreateInt("wildcard_mobswap_end_spawn_delay", 2400)), "2400"));
        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_mobswap_spawn_mobs", 114, "Mob Swap: Number of Mobs", "The number of mobs that spawn each cycle.", List.of(String.valueOf(getOrCreateInt("wildcard_mobswap_spawn_mobs", 250)), "250"));
        NetworkHandlerServer.sendConfig(player, "double", "wildcard_mobswap_boss_chance_multiplier", 115, "Mob Swap: Boss Chance Multiplier", "Multiplier for boss chance (wither / warden).", List.of(String.valueOf(getOrCreateDouble("wildcard_mobswap_boss_chance_multiplier", 1)), "1"));

        NetworkHandlerServer.sendConfig(player, "integer", "wildcard_superpowers_windcharge_max_mace_damage", 116, "Superpower - Wind Charge: Max Mace Damage", "The max amount of damage you can deal with a mace while using the Wind Charge superpower.", List.of(String.valueOf(getOrCreateInt("wildcard_superpowers_windcharge_max_mace_damage", 2)), "2"));
    }
}
