package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;

import java.util.ArrayList;
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
    /*
    public static final ConfigEntry<Boolean> NAME = new ConfigEntry<>(
            "key", 3, "boolean", "", ""
    );
    public static final ConfigEntry<Integer> NAME = new ConfigEntry<>(
            "key", 3, "integer", "", ""
    );
    public static final ConfigEntry<Double> NAME = new ConfigEntry<>(
            "key", 3, "double", "", ""
    );
    */

    public static final ConfigEntry<Double> WILDCARD_SIZESHIFTING_MIN_SIZE = new ConfigEntry<>(
            "wildcard_sizeshifting_min_size", 0.25, "double", "Size Shifting: Min Size", "Smallest size you can achieve during Size Shifting."
    );
    public static final ConfigEntry<Double> WILDCARD_SIZESHIFTING_MAX_SIZE = new ConfigEntry<>(
            "wildcard_sizeshifting_max_size", 3.0, "double", "Size Shifting: Max Size", "Biggest size you can achieve during Size Shifting."
    );
    public static final ConfigEntry<Double> WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER = new ConfigEntry<>(
            "wildcard_sizeshifting_size_change_multiplier", 1.0, "double", "Size Shifting: Change Multiplier", "The speed with which you change your size during Size Shifting."
    );
    public static final ConfigEntry<Integer> WILDCARD_HUNGER_RANDOMIZE_INTERVAL = new ConfigEntry<>(
            "wildcard_hunger_randomize_interval", 36000, "integer", "Hunger: Randomize Interval", "The duration between food changes, in seconds."
    );
    public static final ConfigEntry<Double> WILDCARD_SNAILS_SPEED_MULTIPLIER = new ConfigEntry<>(
            "wildcard_snails_speed_multiplier", 1.0, "double", "Snails: Speed Multiplier", "Snail movement speed multiplier."
    );
    public static final ConfigEntry<Boolean> WILDCARD_SNAILS_DROWN_PLAYERS = new ConfigEntry<>(
            "wildcard_snails_drown_players", true, "boolean", "Snails: Drown Players", "Controls whether snails can drown players when the snails are underwater."
    );


    public static final ConfigEntry<Double> WILDCARD_TIMEDILATION_MIN_SPEED = new ConfigEntry<>(
            "wildcard_timedilation_min_speed", 0.05, "double", "Time Dilation: Min World Speed Multiplier", "Controls the minimum speed the WORLD can move."
    );
    public static final ConfigEntry<Double> WILDCARD_TIMEDILATION_MAX_SPEED = new ConfigEntry<>(
            "wildcard_timedilation_max_speed", 5.0, "double", "Time Dilation: Max World Speed Multiplier", "Controls the maximum speed the WORLD can move."
    );
    public static final ConfigEntry<Double> WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED = new ConfigEntry<>(
            "wildcard_timedilation_player_max_speed", 2.0, "double", "Time Dilation: Max Player Speed Multiplier", "Controls the maximum speed the PLAYERS themselves can move (not the world)."
    );
    public static final ConfigEntry<Boolean> WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS = new ConfigEntry<>(
            "wildcard_trivia_bots_can_enter_boats", true, "boolean", "Trivia: Bots Can Enter Boats", "Controls whether trivia bots can enter boats."
    );
    public static final ConfigEntry<Integer> WILDCARD_TRIVIA_BOTS_PER_PLAYER = new ConfigEntry<>(
            "wildcard_trivia_bots_per_player", 5, "integer", "Trivia: Bots per Player", "The amount of trivia bots that will spawn for each player over the session."
    );
    public static final ConfigEntry<Integer> WILDCARD_TRIVIA_SECONDS_EASY = new ConfigEntry<>(
            "wildcard_trivia_seconds_easy", 180, "integer", "Trivia: Easy Timer", "Easy question timer length, in seconds."
    );
    public static final ConfigEntry<Integer> WILDCARD_TRIVIA_SECONDS_NORMAL = new ConfigEntry<>(
            "wildcard_trivia_seconds_normal", 240, "integer", "Trivia: Normal Timer", "Normal question timer length, in seconds."
    );
    public static final ConfigEntry<Integer> WILDCARD_TRIVIA_SECONDS_HARD = new ConfigEntry<>(
            "wildcard_trivia_seconds_hard", 300, "integer", "Trivia: Hard Timer", "Hard question timer length, in seconds."
    );


    public static final ConfigEntry<Integer> WILDCARD_MOBSWAP_START_SPAWN_DELAY = new ConfigEntry<>(
            "wildcard_mobswap_start_spawn_delay", 7200, "integer", "Mob Swap: Session Start Spawn Delay", "The delay between mob spawns at the START of the session, in seconds."
    );
    public static final ConfigEntry<Integer> WILDCARD_MOBSWAP_END_SPAWN_DELAY = new ConfigEntry<>(
            "wildcard_mobswap_end_spawn_delay", 2400, "integer", "Mob Swap: Session End Spawn Delay", "The delay between mob spawns at the END of the session, in seconds."
    );
    public static final ConfigEntry<Integer> WILDCARD_MOBSWAP_SPAWN_MOBS = new ConfigEntry<>(
            "wildcard_mobswap_spawn_mobs", 250, "integer", "Mob Swap: Number of Mobs", "The number of mobs that spawn each cycle."
    );
    public static final ConfigEntry<Double> WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER = new ConfigEntry<>(
            "wildcard_mobswap_boss_chance_multiplier", 1.0, "double", "Mob Swap: Boss Chance Multiplier", "Multiplier for boss chance (wither / warden)."
    );
    public static final ConfigEntry<Integer> WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE = new ConfigEntry<>(
            "wildcard_superpowers_windcharge_max_mace_damage", 2, "integer", "Superpower - Wind Charge: Max Mace Damage", "The max amount of damage you can deal with a mace while using the Wind Charge superpower."
    );
    public static final ConfigEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS = new ConfigEntry<>(
            "wildcard_superpowers_zombies_lose_items", true, "boolean", "Superpower - Necromancy: Zombies Lose Items", "Controls whether zombies keep their items when they get respawned."
    );
    public static final ConfigEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN = new ConfigEntry<>(
            "wildcard_superpowers_zombies_revive_by_killing_dark_green", false, "boolean", "Superpower - Necromancy: Zombies Can Revive", "Controls whether zombies can be revived (gain a life) by killing a dark green player."
    );
    public static final ConfigEntry<Boolean> WILDCARD_SUPERPOWERS_SUPERSPEED_STEP = new ConfigEntry<>(
            "wildcard_superpowers_superspeed_step", false, "boolean", "Superpower - Superspeed: Step Up Blocks", "Controls whether players with the superspeed power active can step up blocks without jumping (like when riding a horse)."
    );

    public WildLifeConfig() {
        super("./config/"+ Main.MOD_ID,"wildlife.properties");
    }

    @Override
    protected List<ConfigEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                WILDCARD_SIZESHIFTING_MIN_SIZE,
                WILDCARD_SIZESHIFTING_MAX_SIZE,
                WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER,

                WILDCARD_HUNGER_RANDOMIZE_INTERVAL,

                WILDCARD_SNAILS_SPEED_MULTIPLIER,
                WILDCARD_SNAILS_DROWN_PLAYERS,

                WILDCARD_TIMEDILATION_MIN_SPEED,
                WILDCARD_TIMEDILATION_MAX_SPEED,
                WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED,

                WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS,
                WILDCARD_TRIVIA_BOTS_PER_PLAYER,
                WILDCARD_TRIVIA_SECONDS_EASY,
                WILDCARD_TRIVIA_SECONDS_NORMAL,
                WILDCARD_TRIVIA_SECONDS_HARD,

                WILDCARD_MOBSWAP_START_SPAWN_DELAY,
                WILDCARD_MOBSWAP_END_SPAWN_DELAY,
                WILDCARD_MOBSWAP_SPAWN_MOBS,
                WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER,

                WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE,
                WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS,
                WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN,
                WILDCARD_SUPERPOWERS_SUPERSPEED_STEP
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = "["+String.join(", ", BLACKLISTED_ITEMS)+"]";
        BLACKLIST_BLOCKS.defaultValue = "["+String.join(", ", BLACKLISTED_BLOCKS)+"]";
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]";
        DEFAULT_LIVES.defaultValue = 6;
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
        SPAWNER_RECIPE.defaultValue = true;
        TAB_LIST_SHOW_LIVES.defaultValue = true;

        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");

        super.instantiateProperties();
    }
}
