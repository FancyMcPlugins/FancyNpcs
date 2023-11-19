# FancyNpcs Commands

This section procides detailed information about the various commands available in FancyNpcs, allowing you to make the
most out of its features.

## /FancNpcs ...

This command is to manage the plugin itself.

### version

Description: Shows the current version of the plugin<br>
Syntax: ``/FancyNpcs version``<br>
Permission: ``FancyNpcs.admin``

### reload

Description: Reloads the language file and all npcs<br>
Syntax: ``/FancyNpcs reload``<br>
Permission: ``FancyNpcs.admin``

### save

Description: Saves all npcs to disk<br>
Syntax: ``/FancyNpcs save``<br>
Permission: ``FancyNpcs.admin``

### featureFlags

Description: Shows a list of all enabled feature flags<br>
Syntax: ``/FancyNpcs featureFlags``<br>
Permission: ``FancyNpcs.admin``

## /Npc ...

This command is to manage the npcs.

For all subcommands of /npc - ``FancyNpcs.npc.*``<br>

### help

Description: Shows a list of all commands<br>
Syntax: ``/Npc help``<br>
Permission: ``FancyNpcs.npc.help`` or ``FancyNpcs.npc.*``

### create

Description: Creates a new npc at your location<br>
Syntax: ``/Npc create (npc)``<br>
Permission: ``FancyNpcs.npc.create`` or ``FancyNpcs.npc.*``

### remove

Description: Removes the npc<br>
Syntax: ``/Npc remove (npc)``<br>
Permission: ``FancyNpcs.npc.remove`` or ``FancyNpcs.npc.*``

### copy

Description: Creates a copy of the npc<br>
Syntax: ``/Npc copy (npc) (new name)``<br>
Permission: ``FancyNpcs.npc.copy`` or ``FancyNpcs.npc.*``

### list

Description: Shows a list of all created npcs<br>
Syntax: ``/Npc list``<br>
Permission: ``FancyNpcs.npc.list`` or ``FancyNpcs.npc.*``

### type

Description: Changes the entity type of the npc<br>
Syntax: ``/Npc type (npc name) (type)``<br>
Permission: ``FancyNpcs.npc.type`` or ``FancyNpcs.npc.*``

When using any entity type other than Player, the following features are disabled:

- Changing the skin
- Changing the equipment
- Showing in tab

### attribute

Description: Changes a type-specific attribute of the npc<br>
Syntax: ``/Npc attribute (npc name) (attribute) (value)``<br>
Permission: ``FancyNpcs.npc.attribute`` or ``FancyNpcs.npc.*``

### displayName

Description: Changes the displayname of the npc<br>
Syntax: ``/Npc displayName (npc name) (display name ...)``<br>
Permission: ``FancyNpcs.npc.displayName`` or ``FancyNpcs.npc.*``<br>
Placeholders:

- all placeholders from PlaceholderAPI
- ``<empty>`` will make the displayname disappear completely

### showInTab

Description: Changes whether the npc will be shown in the tablist or not<br>
Syntax: ``/Npc showInTab (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.showInTab`` or ``FancyNpcs.npc.*``

### skin

Description: Changes the skin of the npc<br>
Syntax: ``/Npc skin (npc name) (username | url to .png)``<br>
Syntax: ``/Npc skin`` - uses your skin<br>
Permission: ``FancyNpcs.npc.skin`` or ``FancyNpcs.npc.*``

### equipment

Description: Equips the npc with item you are holding in your mainhand<br>
Syntax: ``/Npc equipment (npc name) (slot)``<br>
Permission: ``FancyNpcs.npc.equipment`` or ``FancyNpcs.npc.*``

### glowing

Description: Changes whether the npc should glow or not<br>
Syntax: ``/Npc glowing (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.glowing`` or ``FancyNpcs.npc.*``

### glowingColor

Description: Changes the color of the glowing effect<br>
Syntax: ``/Npc glowingColor (npc name) (color)``<br>
Permission: ``FancyNpcs.npc.glowingColor`` or ``FancyNpcs.npc.*``

### collidable

Description: Changes whether the NPC will be collidable or not<br>
Syntax: ``/Npc collidable (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.collidable`` or ``FancyNpcs.npc.*``

### turnToPlayer

Description: Changes whether the npc will turn to near players or not<br>
Syntax: ``/Npc turnToPlayer (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.turnToPlayer`` or ``FancyNpcs.npc.*``

### message

Description: Changes the message that will be sent to the player when interacting with the npc<br>
Syntax: ``/Npc message (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.message`` or ``FancyNpcs.npc.*``<br>
Placeholders:

- all placeholders from PlaceholderAPI

### playerCommand

Description: Changes the command that the player executes when interacting with the npc<br>
Syntax: ``/Npc playerCommand (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.playerCommand`` or ``FancyNpcs.npc.*``<br>
Placeholders:

- all placeholders from PlaceholderAPI

### serverCommand

Description: Changes the command that the console executes when interacting with the npc<br>
Syntax: ``/Npc serverCommand (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.serverCommand`` or ``FancyNpcs.npc.*``<br>
Placeholders:

- all placeholders from PlaceholderAPI
- ``{player}`` - the player's username

### moveHere

Description: Teleports the npc to you<br>
Syntax: ``/Npc moveHere (npc name)``<br>
Permission: ``FancyNpcs.npc.moveHere`` or ``FancyNpcs.npc.*``

