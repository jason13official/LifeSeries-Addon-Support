package com.cursee.ls_addon_support.config;

import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import java.util.List;

public class DefaultConfigValues {

  public static final List<String> RELOAD_NEEDED = List.of(
      "spawner_recipe"
  );
  public final ConfigFileEntry<Double> SPAWN_EGG_DROP_CHANCE = new ConfigFileEntry<>(
      "spawn_egg_drop_chance", 0.05, ConfigTypes.PERCENTAGE, "global.spawnegg",
      "Spawn Egg Drop Chance", "Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)"
  );
  public final ConfigFileEntry<Boolean> SPAWN_EGG_DROP_ONLY_NATURAL = new ConfigFileEntry<>(
      "spawn_egg_drop_only_natural", true, "global.spawnegg",
      "Spawn Egg Only Natural Drops",
      "Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc)."
  );
  public final ConfigFileEntry<Boolean> CREATIVE_IGNORE_BLACKLIST = new ConfigFileEntry<>(
      "creative_ignore_blacklist", true, "global.blacklist",
      "Creative Ignore Blacklist",
      "Controls whether players in creative mode are able to bypass the blacklists."
  );
  public final ConfigFileEntry<Integer> WORLDBORDER_SIZE = new ConfigFileEntry<>(
      "worldborder_size", 500, "global",
      "Worldborder Size", "Sets the worldborder size."
  );
  public final ConfigFileEntry<Boolean> KEEP_INVENTORY = new ConfigFileEntry<>(
      "keep_inventory", true, "global",
      "Keep Inventory", "Decides whether players drop their items when they die."
  );
  public final ConfigFileEntry<Boolean> PLAYERS_DROP_ITEMS_ON_FINAL_DEATH = new ConfigFileEntry<>(
      "players_drop_items_on_final_death", false, "global.finaldeath",
      "Players Drop Items on Final Death",
      "Controls whether players drop their items on the final death (even if keepInventory is on)."
  );
  public final ConfigFileEntry<Boolean> FINAL_DEATH_TITLE_SHOW = new ConfigFileEntry<>(
      "final_death_title_show", true, "global.finaldeath",
      "Show Death Title on Final Death",
      "Controls whether the death title (the one covering like half the screen) should show up when a player fully dies."
  );
  public final ConfigFileEntry<String> BLACKLIST_BANNED_ENCHANTS = new ConfigFileEntry<>(
      "blacklist_banned_enchants", "[]", ConfigTypes.ENCHANT_LIST, "global.blacklist",
      "Blacklisted Enchants", "List of banned enchants."
  );
  public final ConfigFileEntry<Boolean> MUTE_DEAD_PLAYERS = new ConfigFileEntry<>(
      "mute_dead_players", false, "global",
      "Mute Dead Players", "Controls whether dead players should be allowed to type in chat."
  );
  public final ConfigFileEntry<String> BLACKLIST_BANNED_POTION_EFFECTS = new ConfigFileEntry<>(
      "blacklist_banned_potion_effects", "[strength, instant_health, instant_damage]",
      ConfigTypes.EFFECT_LIST, "global.blacklist",
      "Banned Potion Effects", "List of banned potion effects."
  );
  public final ConfigFileEntry<Boolean> SPAWNER_RECIPE = new ConfigFileEntry<>(
      "spawner_recipe", false, "global.spawnegg",
      "Spawner Recipe", "Controls whether the spawner crafting recipe is enabled."
  );
  public final ConfigFileEntry<Boolean> SPAWN_EGG_ALLOW_ON_SPAWNER = new ConfigFileEntry<>(
      "spawn_egg_allow_on_spawner", false, "global.spawnegg",
      "Spawn Egg Allow on Spawners",
      "Controls whether players should be able to use the spawn eggs on spawners."
  );
  public final ConfigFileEntry<Integer> MAX_PLAYER_HEALTH = new ConfigFileEntry<>(
      "max_player_health", 20, ConfigTypes.HEARTS, "{global.health}",
      "Default Health", "The amount of health (half-hearts) every player will have by default."
  );
  public final ConfigFileEntry<Boolean> SHOW_HEALTH_BELOW_NAME = new ConfigFileEntry<>(
      "show_health_below_name", false, "global.health",
      "Show Health Below Name", "Show the HP a player is on below their username."
  );
  public final ConfigFileEntry<Integer> DEFAULT_LIVES = new ConfigFileEntry<>(
      "default_lives", 3, "global.lives",
      "Default Lives", "The number of lives every player will have by default."
  );
  public final ConfigFileEntry<Boolean> ONLY_TAKE_LIVES_IN_SESSION = new ConfigFileEntry<>(
      "only_take_lives_in_session", false, "global.lives",
      "Only Lose Lives In Session",
      "Makes players only lose lives when they die while a session is active."
  );
  public final ConfigFileEntry<Boolean> CUSTOM_ENCHANTER_ALGORITHM = new ConfigFileEntry<>(
      "custom_enchanter_algorithm", false, "global",
      "Custom Enchanter Algorithm",
      "Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves."
  );
  public final ConfigFileEntry<String> BLACKLIST_ITEMS = new ConfigFileEntry<>(
      "blacklist_items", "[]", ConfigTypes.ITEM_LIST, "global.blacklist",
      "Blacklisted Items", "List of banned items."
  );
  public final ConfigFileEntry<String> BLACKLIST_BLOCKS = new ConfigFileEntry<>(
      "blacklist_blocks", "[]", ConfigTypes.BLOCK_LIST, "global.blacklist",
      "Blacklisted Blocks", "List of banned blocks."
  );
  public final ConfigFileEntry<String> BLACKLIST_CLAMPED_ENCHANTS = new ConfigFileEntry<>(
      "blacklist_clamped_enchants", "[]", ConfigTypes.ENCHANT_LIST, "global.blacklist",
      "Clamped Enchants",
      "List of enchantments clamped to level 1 (any higher levels will be set to lvl1)."
  );
  public final ConfigFileEntry<String> FINAL_DEATH_TITLE_SUBTITLE = new ConfigFileEntry<>(
      "final_death_title_subtitle", "ran out of lives!", "global.finaldeath",
      "Death Subtitle",
      "The subtitle that shows when a player dies (requires Show Death Title on Final Death to be set to true)."
  );
  public final ConfigFileEntry<String> FINAL_DEATH_MESSAGE = new ConfigFileEntry<>(
      "final_death_message", "${player} ran out of lives.", "global.finaldeath",
      "Final Death Message", "The message that gets shown in chat when a player fully dies."
  );
  public final ConfigFileEntry<Boolean> FINAL_DEATH_LIGHTNING = new ConfigFileEntry<>(
      "final_death_lightning", true, "global.finaldeath",
      "Final Death Lightning",
      "Spawns a harmless (no damage) lightning strike when a player fully dies."
  );
  public final ConfigFileEntry<String> FINAL_DEATH_SOUND = new ConfigFileEntry<>(
      "final_death_sound", "minecraft:entity.lightning_bolt.thunder", "global.finaldeath",
      "Final Death Sound", "The sound that gets played to all players when anyone fully dies."
  );
  public final ConfigFileEntry<Boolean> GIVELIFE_COMMAND_ENABLED = new ConfigFileEntry<>(
      "givelife_command_enabled", false, "{global.givelife}",
      "Enable Givelife Command", "Controls whether the '/givelife' command is available."
  );
  public final ConfigFileEntry<Integer> GIVELIFE_LIVES_MAX = new ConfigFileEntry<>(
      "givelife_lives_max", 99, "global.givelife",
      "Max Givelife Lives",
      "The maximum amount of lives a player can have from other players giving them lives using /givelife."
  );
  public final ConfigFileEntry<Boolean> GIVELIFE_BROADCAST = new ConfigFileEntry<>(
      "givelife_broadcast", false, "global.givelife",
      "Broadcast Givelife",
      "Broadcasts the message when a player gives a life to another player using /givelife."
  );
  public final ConfigFileEntry<Boolean> GIVELIFE_CAN_REVIVE = new ConfigFileEntry<>(
      "givelife_can_revive", false, "global.givelife",
      "Givelife Can Revive Dead Players",
      "Controls whether players can revive dead players using /givelife."
  );
  public final ConfigFileEntry<Boolean> TAB_LIST_SHOW_DEAD_PLAYERS = new ConfigFileEntry<>(
      "tab_list_show_dead_players", true, "global",
      "Tab List Show Dead Players", "Controls whether dead players show up in the tab list."
  );
  public final ConfigFileEntry<Boolean> TAB_LIST_SHOW_LIVES = new ConfigFileEntry<>(
      "tab_list_show_lives", false, "{global.lives.showlives}",
      "Tab List Show Lives", "Controls whether you can see the players' lives in the tab list."
  );
  public final ConfigFileEntry<Boolean> TAB_LIST_SHOW_EXACT_LIVES = new ConfigFileEntry<>(
      "tab_list_show_exact_lives", false, "global.lives.showlives",
      "Show EXACT Lives", "Shows the actual number of lives when above 4 instead of just '4+'."
  );
  public final ConfigFileEntry<Boolean> LOCATOR_BAR = new ConfigFileEntry<>(
      "locator_bar", false, "global",
      "Locator Bar", "Enables the player Locator Bar."
  );
  public final ConfigFileEntry<Boolean> BOOGEYMAN = new ConfigFileEntry<>(
      "boogeyman", false, ConfigTypes.BOOGEYMAN, "{global.boogeyman}",
      "Boogeyman Enabled", "Enables the boogeyman."
  );
  public final ConfigFileEntry<Integer> BOOGEYMAN_MIN_AMOUNT = new ConfigFileEntry<>(
      "boogeyman_min_amount", 1, "global.boogeyman",
      "Minimum Boogeyman Amount", "The minimum amount of Boogeymen a session can have."
  );
  public final ConfigFileEntry<Integer> BOOGEYMAN_MAX_AMOUNT = new ConfigFileEntry<>(
      "boogeyman_max_amount", 99, "global.boogeyman",
      "Maximum Boogeyman Amount", "The maximum amount of Boogeymen a session can have."
  );
  public final ConfigFileEntry<String> BOOGEYMAN_IGNORE = new ConfigFileEntry<>(
      "boogeyman_ignore", "[]", "global.boogeyman",
      "Boogeyman Ignore List", "A list of players that cannot become the boogeyman."
  );
  public final ConfigFileEntry<String> BOOGEYMAN_FORCE = new ConfigFileEntry<>(
      "boogeyman_force", "[]", "global.boogeyman",
      "Boogeyman Force List", "A list of players that are forced to become the boogeyman."
  );
  public final ConfigFileEntry<String> BOOGEYMAN_MESSAGE = new ConfigFileEntry<>(
      "boogeyman_message",
      "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman.",
      "global.boogeyman", "Boogeyman Message",
      "The message that shows up when you become a Boogeyman."
  );
  public final ConfigFileEntry<Double> BOOGEYMAN_CHANCE_MULTIPLIER = new ConfigFileEntry<>(
      "boogeyman_chance_multiplier", 0.5, ConfigTypes.PERCENTAGE, "global.boogeyman",
      "Boogeyman Chance Multiplier", "Controls how likely it is to get one extra boogeyman."
  );
  public final ConfigFileEntry<Double> BOOGEYMAN_CHOOSE_MINUTE = new ConfigFileEntry<>(
      "boogeyman_choose_minute", 10.0, ConfigTypes.MINUTES, "global.boogeyman",
      "Boogeyman Choose Time",
      "The number of minutes (in the session) after which the boogeyman gets picked."
  );
  public final ConfigFileEntry<Boolean> BOOGEYMAN_ANNOUNCE_OUTCOME = new ConfigFileEntry<>(
      "boogeyman_announce_outcome", true, "global.boogeyman",
      "Boogeyman Announce Outcome", "Shows a message in chat when the boogeyman succeeds or fails."
  );
  public final ConfigFileEntry<Boolean> BOOGEYMAN_INFINITE = new ConfigFileEntry<>(
      "boogeyman_infinite", false, "{global.boogeyman.infinite}",
      "Boogeyman Infinite Rolling",
      "When any boogeyman is cured, a new one will replace them immediatelly."
  );
  public final ConfigFileEntry<Integer> BOOGEYMAN_INFINITE_LAST_PICK = new ConfigFileEntry<>(
      "boogeyman_infinite_last_pick", 1800, ConfigTypes.SECONDS, "global.boogeyman.infinite",
      "Last Roll Before End Of Session",
      "Controls how long before the end of session the infinite boogey picking will stop, in seconds."
  );
  public final ConfigFileEntry<Boolean> SECRET_SOCIETY = new ConfigFileEntry<>(
      "secret_society", false, "{global.society}",
      "Secret Society Enabled", "Enables the Secret Society in the session."
  );
  public final ConfigFileEntry<Integer> SECRET_SOCIETY_MEMBER_AMOUNT = new ConfigFileEntry<>(
      "secret_society_member_amount", 3, "global.society",
      "Member Amount", "The number of players that are a part of the Secret Society"
  );
  public final ConfigFileEntry<Double> SECRET_SOCIETY_START_TIME = new ConfigFileEntry<>(
      "secret_society_start_time", 5.0, ConfigTypes.MINUTES, "global.society",
      "Society Start Time", "Controls when in the session the Society will start, in minutes."
  );
  public final ConfigFileEntry<String> SECRET_SOCIETY_FORCE = new ConfigFileEntry<>(
      "secret_society_force", "[]", "global.society",
      "Member Force List", "A list of players that are forced to become a Member in the society."
  );
  public final ConfigFileEntry<String> SECRET_SOCIETY_IGNORE = new ConfigFileEntry<>(
      "secret_society_ignore", "[]", "global.society",
      "Member Ignore List", "A list of players that cannot become a Member in the society."
  );
  public final ConfigFileEntry<String> SECRET_SOCIETY_WORDS = new ConfigFileEntry<>(
      "secret_society_words", "[Hammer, Magnet, Throne, Gravity, Puzzle, Spiral, Pivot, Flare]",
      "global.society",
      "Random Words", "List of words that can be picked as the secret word."
  );
  public final ConfigFileEntry<Boolean> WATCHERS_IN_TAB = new ConfigFileEntry<>(
      "watchers_in_tab", true, "global.watchers",
      "Show Watchers In Tab", "Controls whether Watchers should show up in the tab list."
  );
  public final ConfigFileEntry<Boolean> WATCHERS_MUTED = new ConfigFileEntry<>(
      "watchers_muted", false, "global.watchers",
      "Mute Watchers", "Controls whether the Watchers should be allowed to type in chat."
  );
  public final ConfigFileEntry<Boolean> ALLOW_SELF_DEFENSE = new ConfigFileEntry<>(
      "allow_self_defense", true, "global",
      "Allow Self Defense Kills", "Controls whether self-defense kills should count as unjustified."
  );
  public final ConfigFileEntry<Boolean> SEE_FRIENDLY_INVISIBLE_PLAYERS = new ConfigFileEntry<>(
      "see_friendly_invisible_players", false, "global",
      "See Friendly Invisible Players",
      "Controls whether players can see other invisible players on the same life color."
  );
  public final ConfigFileEntry<Boolean> SHOW_LOGIN_COMMAND_INFO = new ConfigFileEntry<>(
      "show_login_command_info", true, "global",
      "Show Command Info On Login",
      "Controls whether players get a message in chat showing the available commands when the login."
  );
  /*
   * Group Entries
   */
  public final ConfigFileEntry<Object> GROUP_GLOBAL = new ConfigFileEntry<>(
      "group_global", null, ConfigTypes.TEXT, "{global}[no_sidebar, closed]",
      "General Settings", ""
  );
  public final ConfigFileEntry<Object> GROUP_SEASON = new ConfigFileEntry<>(
      "group_season", null, ConfigTypes.TEXT, "{season}[no_sidebar, closed]",
      "Season Specific Settings", ""
  );
  public final ConfigFileEntry<Object> GROUP_LIVES = new ConfigFileEntry<>(
      "group_lives", null, ConfigTypes.TEXT, "{global.lives}",
      "Lives Stuff", ""
  );
  public final ConfigFileEntry<Object> GROUP_BLACKLIST = new ConfigFileEntry<>(
      "group_blacklist", null, ConfigTypes.TEXT, "{global.blacklist}",
      "Blacklists", ""
  );
  public final ConfigFileEntry<Object> GROUP_FINAL_DEATH = new ConfigFileEntry<>(
      "group_final_death", null, ConfigTypes.TEXT, "{global.finaldeath}",
      "Final Death", ""
  );
  public final ConfigFileEntry<Object> GROUP_SPAWN_EGG = new ConfigFileEntry<>(
      "group_spawn_egg", null, ConfigTypes.TEXT, "{global.spawnegg}",
      "Spawn Egg", ""
  );
  public final ConfigFileEntry<Object> GROUP_WATCHERS = new ConfigFileEntry<>(
      "group_watchers", null, ConfigTypes.TEXT, "{global.watchers}",
      "Watchers §7('/watcher' command)", ""
  );
}
