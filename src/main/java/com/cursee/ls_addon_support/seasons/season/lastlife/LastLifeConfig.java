package com.cursee.ls_addon_support.seasons.season.lastlife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ConfigFileEntry;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.utils.other.TextUtils;
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


  public static final ConfigFileEntry<Integer> RANDOM_LIVES_MIN = new ConfigFileEntry<>(
      "random_lives_min", 2, "season",
      "Random Lives Min", "The minumum lives you can get from the random roll."
  );
  public static final ConfigFileEntry<Integer> RANDOM_LIVES_MAX = new ConfigFileEntry<>(
      "random_lives_max", 6, "season",
      "Random Lives Max", "The maximum lives you can get from the random roll."
  );


  public LastLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID, "lastlife.properties");
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
        RANDOM_LIVES_MIN
        , RANDOM_LIVES_MAX
    ));
  }

  @Override
  public void instantiateProperties() {
    CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
    BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
    BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
    BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
    GIVELIFE_COMMAND_ENABLED.defaultValue = true;
    BOOGEYMAN.defaultValue = true;
    super.instantiateProperties();
  }
}
