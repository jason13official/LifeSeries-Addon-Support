package net.mat0u5.lifeseries.series.limitedlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.config.ConfigEntry;
import net.mat0u5.lifeseries.resources.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class LimitedLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "mace",
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

    public static final ConfigEntry<Integer> BOOGEYMAN_AMOUNT = new ConfigEntry<>(
            "boogeyman_amount", 1, "integer", "Boogeyman Amount", "The exact amount of bogeymen for each session."
    );
    public static final ConfigEntry<Integer> TIME_DEFAULT = new ConfigEntry<>(
            "time_default", 86400, "integer", "Time Default", "The time with which players start, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_YELLOW = new ConfigEntry<>(
            "time_yellow", 57600, "integer", "Time Yellow", "The Green-Yellow time border, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_RED = new ConfigEntry<>(
            "time_red", 28800, "integer", "Time Red", "The Yellow-Red time border, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_DEATH = new ConfigEntry<>(
            "time_death", -3600, "integer", "Time Death", "Time time you lose for dying, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_DEATH_BOOGEYMAN = new ConfigEntry<>(
            "time_death_boogeyman", -7200, "integer", "Time Death Boogeyman", "The time you lose for the Boogeyman killing you, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_KILL = new ConfigEntry<>(
            "time_kill", 1800, "integer", "Time Kill", "The time you gain for killing someone, in seconds."
    );
    public static final ConfigEntry<Integer> TIME_KILL_BOOGEYMAN = new ConfigEntry<>(
            "time_kill_boogeyman", 3600, "integer", "Time Kill Boogeyman", "The time you gain for killing someone while you are the boogeyman, in seconds."
    );
    public static final ConfigEntry<Boolean> TICK_OFFLINE_PLAYERS = new ConfigEntry<>(
            "tick_offline_players", false, "boolean", "Tick Offline Players", "Controls whether even players that are offline lose time when the session is on."
    );

    public LimitedLifeConfig() {
        super("./config/"+ Main.MOD_ID,"limitedlife.properties");
    }

    @Override
    protected List<ConfigEntry<?>> getDefaultConfigEntries() {
        List<ConfigEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(DEFAULT_LIVES);
        defaultEntries.remove(GIVELIFE_COMMAND_ENABLED);
        defaultEntries.remove(GIVELIFE_LIVES_MAX);
        return defaultEntries;
    }

    @Override
    protected List<ConfigEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                BOOGEYMAN_AMOUNT,
                TIME_DEFAULT,
                TIME_YELLOW,
                TIME_RED,
                TIME_DEATH,
                TIME_DEATH_BOOGEYMAN,
                TIME_KILL,
                TIME_KILL_BOOGEYMAN,
                TICK_OFFLINE_PLAYERS
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = "["+String.join(", ", BLACKLISTED_ITEMS)+"]";
        BLACKLIST_BLOCKS.defaultValue = "["+String.join(", ", BLACKLISTED_BLOCKS)+"]";
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]";
        FINAL_DEATH_TITLE_SUBTITLE.defaultValue = "ran out of time!";
        FINAL_DEATH_MESSAGE.defaultValue = "${player} ran out of time.";
        super.instantiateProperties();
    }
}
