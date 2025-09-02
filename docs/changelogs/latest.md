# Full changelog from version `1.3.7.2` to `*`.

----------

'wildcard_hunger_randomize_interval', 'wildcard_mobswap_start_spawn_delay' and 'wildcard_mobswap_end_spawn_delay' configs are now ACTUALLY in seconds, not in ticks.

----------

Fixed some scaled textures not being properly cut off.<br>
When a zombie gets revived in Wild Life by killing a dark green, the message that shows they gained a life now properly shows up.<br>
Added the 'wildcard_superpowers_disable_intro_theme' Wild Life config. Default value is false.

----------

Added the ability to set your in-game hearts color based on the number of lives you have (or rather your team color).<br>
This is controlled by the clien-side 'colored_hearts' config. Default value is false.<br>
Works with all 16 possible team colors.<br>
Not compatible with Secret Life.<br>
Added the 'colored_hearts_hardcore_last_life' client-side config. Default value is true.<br>
Added the 'colored_hearts_hardcore_all_lives' client-side config. Default value is false.

----------

Watchers can no longer be soulmates.<br>
Zombies no longer lose their items in between revives.<br>
Zombies no longer sometimes spawn without full health

----------

Added the StringListConfigEntry config type, which only allows certain string values in its list. (similar to item/block config entries, just more flexible)<br>
Added the 'wildcard_callback_wildcards_blacklist' Wild Life config entry. Default value is '[hunger]'.<br>
Fixed a bug in the config gui where text field config entries sometimes would not allow you to save changes even if they didn't have an error value.

----------

Snails and Trivia Bots can no longer go through nether (and end) portals.<br>
Added the 'wildcard_superpowers_power_blacklist' Wild Life config, which allows you to prevent some powers from showing up from randomizations. Default value is '[]'.

----------

Added the 'breakup_last_pair_standing' Double Life config. Default value is false.<br>
Added the 'disable_start_teleport' Double Life config. Default value is false.<br>
Added the 'soulbound_locator_bar' Double Life config. Default value is false.

----------

Added the 'boogeyman_infinite' global config. After all boogeymen have been cured, new ones will be selected if there is more than 30 minutes in the session. Default value is false.<br>
Added the 'boogeyman_infinite_last_pick' global config. Controls how long before the end of session the infinite boogey picking will stop, in seconds. Default value is 1800.

----------

Session transcripts now get saved in a folder `./transcripts`.<br>
Session transcripts now track gifted lives and hearts, and starting and ending health in Secret Life.<br>
Colored hearts now work in Secret Life.

----------

Resourcepacks are now marked as compatible with all the versions.<br>
Removed the client-side Secret Life resourcepack, all of its functionality is now handled elsewhere.<br>
Added the red task totem texture to Secret Life.

----------

Added Past Life.<br>
Added a new info screen when Past Life is selected, saying that there's a separate project on modrinth if you want to play with the versions aspect of Past Life.

----------

Wild Life animal disguise is no longer broken with Fresh Animations (still kinda broken tho xd).<br>
Setting the last life min lives to >= the max lives no longer crashes the game.

----------

Added the '/boogeyman count' command.<br>
Added the 'only_take_lives_in_session' global config. Default value is false.

----------

Added the 'givelife_can_revive' global config. Default value is false.<br>
Added the 'see_friendly_invisible_players' global config. Default value is false.<br><br>
Added the 'wildcard_callback_turn_off' Wild Life config. Default value is 75%.<br>
Added the 'wildcard_hunger_nutrition_chance' Wild Life config. Default value is 40%.<br>
Added the 'wildcard_hunger_saturation_chance' Wild Life config. Default value is 50%.<br>
Added the 'wildcard_hunger_effect_chance' Wild Life config. Default value is 65%.<br>
Added the 'wildcard_hunger_avg_effect_duration' Wild Life config. Default value is 10 (seconds).

----------

Added the 'soulbound_boogeyman' Double Life config. Default value is false.

----------

Red players can now become boogeymen in Limited Life (non-reds have priority)<br>
Added the 'show_login_command_info' global config, which controls whether the command info message shows up when players login. Default value is true.<br>
Players hidden from the tab list using the config now show up in commands (requires the mod to be client-side).