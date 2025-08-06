# Full changelog from version `1.3.6.5` to `*`.

----------

The client now resets stored data when it logs off the server.

----------

Soulmates in Double Life no longer get damage ticked when you take damage while blocking with a shield.<br>
Improved the `/boogeyman list` command.

----------

Fixed a bug where the client would crash when the hearts in the config entry were negative.<br>
Made some Secret Life configs heart config entries.<br>

Added config entries for seconds and minutes, which show the readable time in the popup above the textbox.<br>
Lots of configs from Limited Life, Secret Life, Wild Life are now these types.

----------

Added a Click to expand/collapse text to text config entries which are group headers.<br>
Added the 'givelife_broadcast' global config, which controls whether the "Player1 received a life from Player2" message shows up in chat. Default value is false.

----------

The timers and cooldowns in the mod no longer show up when you press F1.<br>
Added the 'broadcast_color_changes' Limited Life config, which sends a message in chat to all players when someone changes color. Default value is false.

----------

Added the Real Life season cuz why not. It's identical to Third Life, just has a separate config and you can select it in the april fools seasons menu.

----------

The creative mode ignores blacklist config now works even when players in creative open chests or try to pick up items from the ground.<br>
Added the 'max_player_kill_health' Secret Life config, which controls the maximum health a player can have after killing another player. Default value is 1000 (so basically uncapped).

----------

Added the 'announce_soulmates' config to Double Life, which shows the soulmate name when they are rolled. Default value is false.<br>
Added the 'boogeyman_announce_outcome' global config, which broadcasts a message in chat when the boogeyman fails or is cured. Default value is true.<br>
When the session ends and the boogeyman is offline, it now actually removes their lives instead of telling the adming to do it when the player logs on again.<br>
Killing a boogeyman in Double Life no longer cures them (they used to be cured because technically they caused the death of their soulmate)

----------

Added the 'broadcast_secret_keeper' Secret Life Config, which shows a message in chat when anyone succeeds, fails or rerolls a task. Default value is false.<br>
Added the 'broadcast_life_gain' Wild Life config, which shows a message in chat when someone gains a life by killing a dark green. Default value is false.

----------

In Double Life, all soulmates are now given a tag, `soulmate_<index>` where the index is determined by sorting all players by UUID and assigning consecutive numbers to each pair (starting from 1)<br>
Wild Life zombies are periodically checked to be alive, and if so, they are internally removed from the zombies list and given back the default number of hearts.<br>
Added the 'tab_list_show_exact_lives' config, which controls whether to show '4+' or the exact number of lives in the tab list when above four. Default value is false.

----------

Swapped Real Life and Simple Life order in the choose april fools season menu.<br>
Added the 'soulbound_food' config to Double Life, which makes you share a hunger bar with your soulmate just like the health bar. Default value is false.
