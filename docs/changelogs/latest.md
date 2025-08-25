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