package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
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

    public static final ConfigFileEntry<Boolean> ANNOUNCE_SOULMATES = new ConfigFileEntry<>(
            "announce_soulmates", false, "season", "Announce Soulmates", "Tells you who your soulmate is instead of it saying 'Your soulmate is ????'"
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_FOOD = new ConfigFileEntry<>(
            "soulbound_food", false, "season", "Soulbound Food", "Makes your food bar shared with your soulmate, just like the health bar."
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_EFFECTS = new ConfigFileEntry<>(
            "soulbound_effects", false, "season", "Soulbound Effects", "Makes your effects be shared with your soulmate."
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_INVENTORIES = new ConfigFileEntry<>(
            "soulbound_inventories", false, "season", "Soulbound Inventories", "Makes your inventory be shared with your soulmate. \nWARNING: There could be some ways of abusing this (duping etc). Use with caution."
    );

    public DoubleLifeConfig() {
        super("./config/"+ Main.MOD_ID,"doublelife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                ANNOUNCE_SOULMATES
                ,SOULBOUND_FOOD
                ,SOULBOUND_EFFECTS
                ,SOULBOUND_INVENTORIES
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        super.instantiateProperties();
    }
}
