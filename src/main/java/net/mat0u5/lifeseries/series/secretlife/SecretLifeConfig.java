package net.mat0u5.lifeseries.series.secretlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.config.ConfigEntry;
import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.server.network.ServerPlayerEntity;

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


    public static final ConfigEntry<Boolean> PLAYERS_DROP_TASK_ON_DEATH = new ConfigEntry<>(
            "players_drop_task_on_death", false, "boolean", "Drop Task On Death", "Decides whether players drop their secret task book on death or if they keep it."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_EASY_PASS = new ConfigEntry<>(
            "task_health_easy_pass", 20, "integer", "Task Health Easy Pass", "The health you gain for passing an easy task."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_EASY_FAIL = new ConfigEntry<>(
            "task_health_easy_fail", 0, "integer", "Task Health Easy Fail", "The health you lose for failing an easy task."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_HARD_PASS = new ConfigEntry<>(
            "task_health_hard_pass", 40, "integer", "Task Health Hard Pass", "The health you gain for passing a hard task."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_HARD_FAIL = new ConfigEntry<>(
            "task_health_hard_fail", -20, "integer", "Task Health Hard Fail", "The health you lose for failing a hard task."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_RED_PASS = new ConfigEntry<>(
            "task_health_red_pass", 10, "integer", "Task Health Red Pass", "The health you gain for passing a red task."
    );
    public static final ConfigEntry<Integer> TASK_HEALTH_RED_FAIL = new ConfigEntry<>(
            "task_health_red_fail", -5, "integer", "Task Health Red Fail", "The health you lose for failing a red task."
    );

    public SecretLifeConfig() {
        super("./config/"+ Main.MOD_ID,"secretlife.properties");
    }

    @Override
    protected List<ConfigEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                PLAYERS_DROP_TASK_ON_DEATH,
                TASK_HEALTH_EASY_PASS,
                TASK_HEALTH_EASY_FAIL,
                TASK_HEALTH_HARD_PASS,
                TASK_HEALTH_HARD_FAIL,
                TASK_HEALTH_RED_PASS,
                TASK_HEALTH_RED_FAIL
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
