package com.cursee.ls_addon_support.seasons.season.secretlife;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.config.ConfigFileEntry;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class SecretLifeConfig extends ConfigManager {

  public static final List<String> BLACKLISTED_ITEMS = List.of(
      "lectern",
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
      "lectern"
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

      "thorns",

      "breach",
      "density",
      "wind_burst",

      "multishot",
      "piercing",
      "quick_charge"
  );


  public static final ConfigFileEntry<Boolean> PLAYERS_DROP_TASK_ON_DEATH = new ConfigFileEntry<>(
      "players_drop_task_on_death", false, "season",
      "Drop Task On Death",
      "Decides whether players drop their secret task book on death or if they keep it."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_PASS = new ConfigFileEntry<>(
      "task_health_easy_pass", 20, ConfigTypes.HEARTS, "season.health",
      "Task Health Easy Pass", "The health you gain for passing an easy task."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_FAIL = new ConfigFileEntry<>(
      "task_health_easy_fail", 0, ConfigTypes.HEARTS, "season.health",
      "Task Health Easy Fail", "The health you lose for failing an easy task."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_PASS = new ConfigFileEntry<>(
      "task_health_hard_pass", 40, ConfigTypes.HEARTS, "season.health",
      "Task Health Hard Pass", "The health you gain for passing a hard task."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_FAIL = new ConfigFileEntry<>(
      "task_health_hard_fail", -20, ConfigTypes.HEARTS, "season.health",
      "Task Health Hard Fail", "The health you lose for failing a hard task."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_PASS = new ConfigFileEntry<>(
      "task_health_red_pass", 10, ConfigTypes.HEARTS, "season.health",
      "Task Health Red Pass", "The health you gain for passing a red task."
  );
  public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_FAIL = new ConfigFileEntry<>(
      "task_health_red_fail", -5, ConfigTypes.HEARTS, "season.health",
      "Task Health Red Fail", "The health you lose for failing a red task."
  );
  public static final ConfigFileEntry<Double> ASSIGN_TASKS_MINUTE = new ConfigFileEntry<>(
      "assign_tasks_minute", 1.0, ConfigTypes.MINUTES, "season",
      "Assign Tasks Time",
      "The number of minutes (in the session) after which the tasks get assigned."
  );
  public static final ConfigFileEntry<Integer> MAX_PLAYER_KILL_HEALTH = new ConfigFileEntry<>(
      "max_player_kill_health", 1000, ConfigTypes.HEARTS, "season",
      "Max Health After Killing",
      "The maximum health a player can have after killing a player (since killing gives hearts)."
  );
  public static final ConfigFileEntry<Boolean> BROADCAST_SECRET_KEEPER = new ConfigFileEntry<>(
      "beoadcast_secret_keeper", false, "season",
      "Secret Keeper Broadcast",
      "Makes the secret keeper broadcast a message in chat when someone succeeds, fails or rerolls a task."
  );

  public static final ConfigFileEntry<Object> GROUP_HEALTH = new ConfigFileEntry<>(
      "group_health", null, ConfigTypes.TEXT, "{season.health}",
      "Health Rewards / Punishments", ""
  );

  public SecretLifeConfig() {
    super("./config/" + LSAddonSupport.MOD_ID, "secretlife.properties");
  }

  @Override
  protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
    return new ArrayList<>(List.of(
        PLAYERS_DROP_TASK_ON_DEATH
        , ASSIGN_TASKS_MINUTE
        , MAX_PLAYER_KILL_HEALTH
        , BROADCAST_SECRET_KEEPER

        , GROUP_HEALTH //Group

        //Group stuff
        , TASK_HEALTH_EASY_PASS
        , TASK_HEALTH_EASY_FAIL
        , TASK_HEALTH_HARD_PASS
        , TASK_HEALTH_HARD_FAIL
        , TASK_HEALTH_RED_PASS
        , TASK_HEALTH_RED_FAIL
    ));
  }

  @Override
  public void instantiateProperties() {
    BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
    BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
    BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
    MAX_PLAYER_HEALTH.defaultValue = 60;
    SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
    SPAWNER_RECIPE.defaultValue = true;
    super.instantiateProperties();
  }
}
