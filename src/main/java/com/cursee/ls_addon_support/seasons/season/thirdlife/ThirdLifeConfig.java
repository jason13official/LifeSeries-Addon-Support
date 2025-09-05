package com.cursee.ls_addon_support.seasons.season.thirdlife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class ThirdLifeConfig extends ConfigManager {

  public static final List<String> BLACKLISTED_ITEMS = List.of(
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

  public static final List<String> BLACKLISTED_BLOCKS = new ArrayList<>();
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

  public ThirdLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID, "thirdlife.properties");
  }

  public ThirdLifeConfig(String folderPath, String filePath) {
    super(folderPath, filePath);
  }

  @Override
  public void instantiateProperties() {
    BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
    BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
    BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
    super.instantiateProperties();
  }
}
