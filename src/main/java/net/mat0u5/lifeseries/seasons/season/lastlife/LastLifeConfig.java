package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;

import java.util.ArrayList;
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


    public static final ConfigFileEntry<Double> BOOGEYMAN_CHANCE_MULTIPLIER = new ConfigFileEntry<>(
            "boogeyman_chance_multiplier", 1.0, "double", "season", "Boogeyman Change Multiplier", "Increases or decreases the average amount of Boogeymen for each session. (If the multiplier is set to the default of 1, sessions will have two Boogeymen on average.)"
    );
    public static final ConfigFileEntry<Integer> BOOGEYMAN_MAX_AMOUNT = new ConfigFileEntry<>(
            "boogeyman_max_amount", 999, "integer", "season", "Boogeyman Max Amount", "The maximum amount of Boogeymen a session can have."
    );
    public static final ConfigFileEntry<Integer> RANDOM_LIVES_MIN = new ConfigFileEntry<>(
            "random_lives_min", 2, "integer", "season", "Random Lives Min", "The minumum lives you can get from the random roll."
    );
    public static final ConfigFileEntry<Integer> RANDOM_LIVES_MAX = new ConfigFileEntry<>(
            "random_lives_max", 6, "integer", "season", "Random Lives Max", "The maximum lives you can get from the random roll."
    );


    public LastLifeConfig() {
        super("./config/"+ Main.MOD_ID,"lastlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(DEFAULT_LIVES);
        return defaultEntries;
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                BOOGEYMAN_CHANCE_MULTIPLIER
                ,BOOGEYMAN_MAX_AMOUNT
                ,RANDOM_LIVES_MIN
                ,RANDOM_LIVES_MAX
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = "["+String.join(", ", BLACKLISTED_ITEMS)+"]";
        BLACKLIST_BLOCKS.defaultValue = "["+String.join(", ", BLACKLISTED_BLOCKS)+"]";
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]";
        GIVELIFE_COMMAND_ENABLED.defaultValue = true;
        super.instantiateProperties();
    }
}
