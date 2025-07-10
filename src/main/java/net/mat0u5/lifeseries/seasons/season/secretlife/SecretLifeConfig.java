package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;

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
            "players_drop_task_on_death", false, "boolean", "season", "Drop Task On Death", "Decides whether players drop their secret task book on death or if they keep it."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_PASS = new ConfigFileEntry<>(
            "task_health_easy_pass", 20, "integer", "season.health", "Task Health Easy Pass", "The health you gain for passing an easy task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_FAIL = new ConfigFileEntry<>(
            "task_health_easy_fail", 0, "integer", "season.health", "Task Health Easy Fail", "The health you lose for failing an easy task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_PASS = new ConfigFileEntry<>(
            "task_health_hard_pass", 40, "integer", "season.health", "Task Health Hard Pass", "The health you gain for passing a hard task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_FAIL = new ConfigFileEntry<>(
            "task_health_hard_fail", -20, "integer", "season.health", "Task Health Hard Fail", "The health you lose for failing a hard task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_PASS = new ConfigFileEntry<>(
            "task_health_red_pass", 10, "integer", "season.health", "Task Health Red Pass", "The health you gain for passing a red task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_FAIL = new ConfigFileEntry<>(
            "task_health_red_fail", -5, "integer", "season.health", "Task Health Red Fail", "The health you lose for failing a red task."
    );

    public static final ConfigFileEntry<Object> GROUP_HEALTH = new ConfigFileEntry<>(
            "group_health", null, "text", "{season.health}", "Health Rewards / Punishments", ""
    );

    public SecretLifeConfig() {
        super("./config/"+ Main.MOD_ID,"secretlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                PLAYERS_DROP_TASK_ON_DEATH

                ,GROUP_HEALTH //Group

                //Group stuff
                ,TASK_HEALTH_EASY_PASS
                ,TASK_HEALTH_EASY_FAIL
                ,TASK_HEALTH_HARD_PASS
                ,TASK_HEALTH_HARD_FAIL
                ,TASK_HEALTH_RED_PASS
                ,TASK_HEALTH_RED_FAIL
        ));
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = "["+String.join(", ", BLACKLISTED_ITEMS)+"]";
        BLACKLIST_BLOCKS.defaultValue = "["+String.join(", ", BLACKLISTED_BLOCKS)+"]";
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]";
        MAX_PLAYER_HEALTH.defaultValue = 60;
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
        SPAWNER_RECIPE.defaultValue = true;
        super.instantiateProperties();
    }
}
