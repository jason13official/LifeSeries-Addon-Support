package com.cursee.ls_addon_support.seasons.season.doublelife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ConfigFileEntry;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.TextUtils;
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
      "announce_soulmates", false, "season",
      "Announce Soulmates",
      "Tells you who your soulmate is instead of it saying 'Your soulmate is ????'"
  );

  public static final ConfigFileEntry<Boolean> SOULBOUND_FOOD = new ConfigFileEntry<>(
      "soulbound_food", false, "season.soulbind",
      "Soulbound Food", "Makes your food bar shared with your soulmate, just like the health bar."
  );
  public static final ConfigFileEntry<Boolean> SOULBOUND_EFFECTS = new ConfigFileEntry<>(
      "soulbound_effects", false, "season.soulbind",
      "Soulbound Effects", "Makes your effects be shared with your soulmate."
  );
  public static final ConfigFileEntry<Boolean> SOULBOUND_INVENTORIES = new ConfigFileEntry<>(
      "soulbound_inventories", false, "season.soulbind",
      "Soulbound Inventories",
      "Makes your inventory be shared with your soulmate. \nWARNING: There could be some ways of abusing this (duping etc). Use with caution."
  );
  public static final ConfigFileEntry<Boolean> BREAKUP_LAST_PAIR_STANDING = new ConfigFileEntry<>(
      "breakup_last_pair_standing", false, "season",
      "Breakup Last Pair Standing",
      "Once only two players are left, they will be broken up as soulmates for a final showdown."
  );
  public static final ConfigFileEntry<Boolean> DISABLE_START_TELEPORT = new ConfigFileEntry<>(
      "disable_start_teleport", false, "season",
      "Disable Start Teleport",
      "Disables the player spreading over the map when the first session starts."
  );
  public static final ConfigFileEntry<Boolean> SOULMATE_LOCATOR_BAR = new ConfigFileEntry<>(
      "soulbound_locator_bar", false, "season",
      "Soulmate Locator Bar", "Makes ONLY your soulmate appear on the locator bar."
  );
  public static final ConfigFileEntry<Boolean> SOULBOUND_BOOGEYMAN = new ConfigFileEntry<>(
      "soulbound_boogeyman", false, "season.soulbind",
      "Soulbound Boogeyman (If enabled)",
      "Makes you become the Bogeyman if your soulmate is one - curing one will cure the other as well."
  );


  public static final ConfigFileEntry<Object> GROUP_SOULBIND = new ConfigFileEntry<>(
      "group_soulbind", null, ConfigTypes.TEXT, "{season.soulbind}",
      "More Soulbind Options", ""
  );

  public DoubleLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID, "doublelife.properties");
  }

  @Override
  protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
    List<ConfigFileEntry<?>> result = new ArrayList<>(List.of(
        ANNOUNCE_SOULMATES
        , GROUP_SOULBIND //Group
        , BREAKUP_LAST_PAIR_STANDING
        , DISABLE_START_TELEPORT

        , SOULBOUND_FOOD
        , SOULBOUND_EFFECTS
        , SOULBOUND_INVENTORIES
        , SOULBOUND_BOOGEYMAN
    ));
    //? if >= 1.21.6 {
    /*result.add(SOULMATE_LOCATOR_BAR);
     *///?}
    return result;
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
