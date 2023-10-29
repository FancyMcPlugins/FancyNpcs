# Commands

<!-- TOC -->

* [Commands](#commands)
    * [/FancNpcs ...](#fancnpcs-)
        * [version](#version)
        * [reload](#reload)
        * [save](#save)
        * [featureFlags](#featureflags)
    * [/Npc ...](#npc-)
        * [help](#help)
        * [create](#create)
        * [remove](#remove)
        * [copy](#copy)
        * [list](#list)
        * [type](#type)
        * [attribute](#attribute)
        * [displayName](#displayname)
        * [showInTab](#showintab)
        * [skin](#skin)
        * [equipment](#equipment)
        * [glowing](#glowing)
        * [glowingColor](#glowingcolor)
        * [collidable](#collidable)
        * [turnToPlayer](#turntoplayer)
        * [message](#message)
        * [playerCommand](#playercommand)
        * [serverCommand](#servercommand)
        * [moveHere](#movehere)

<!-- TOC -->

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

### help

Description: Shows a list of all commands<br>
Syntax: ``/Npc help``<br>
Permission: ``FancyNpcs.npc.help``

### create

Description: Creates a new npc at your location<br>
Syntax: ``/Npc create (npc)``<br>
Permission: ``FancyNpcs.npc.create``

### remove

Description: Removes the npc<br>
Syntax: ``/Npc remove (npc)``<br>
Permission: ``FancyNpcs.npc.remove``

### copy

Description: Creates a copy of the npc<br>
Syntax: ``/Npc copy (npc) (new name)``<br>
Permission: ``FancyNpcs.npc.copy``

### list

Description: Shows a list of all created npcs<br>
Syntax: ``/Npc list``<br>
Permission: ``FancyNpcs.npc.list``

### type

Description: Changes the entity type of the npc<br>
Syntax: ``/Npc type (npc name) (type)``<br>
Permission: ``FancyNpcs.npc.type``

### attribute

Description: Changes a type-specific attribute of the npc<br>
Syntax: ``/Npc attribute (npc name) (attribute) (value)``<br>
Permission: ``FancyNpcs.npc.attribute``

### displayName

Description: Changes the displayname of the npc<br>
Syntax: ``/Npc displayName (npc name) (display name ...)``<br>
Permission: ``FancyNpcs.npc.displayName``<br>
Placeholders:

- all placeholders from PlaceholderAPI
- ``<empty>`` will make the displayname disappear completely

### showInTab

Description: Changes whether the npc will be shown in the tablist or not<br>
Syntax: ``/Npc showInTab (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.showInTab``

### skin

Description: Changes the skin of the npc<br>
Syntax: ``/Npc skin (npc name) (username | url to .png)``<br>
Syntax: ``/Npc skin`` - uses your skin<br>
Permission: ``FancyNpcs.npc.skin``

### equipment

Description: Equips the npc with item you are holding in your mainhand<br>
Syntax: ``/Npc equipment (npc name) (slot)``<br>
Permission: ``FancyNpcs.npc.equipment``

### glowing

Description: Changes whether the npc should glow or not<br>
Syntax: ``/Npc glowing (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.glowing``

### glowingColor

Description: Changes the color of the glowing effect<br>
Syntax: ``/Npc glowingColor (npc name) (color)``<br>
Permission: ``FancyNpcs.npc.glowingColor``

### collidable

Description: Changes whether the NPC will be collidable or not<br>
Syntax: ``/Npc collidable (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.collidable``

### turnToPlayer

Description: Changes whether the npc will turn to near players or not<br>
Syntax: ``/Npc turnToPlayer (npc name) ('true' | 'false')``<br>
Permission: ``FancyNpcs.npc.turnToPlayer``

### message

Description: Changes the message that will be sent to the player when interacting with the npc<br>
Syntax: ``/Npc message (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.message``<br>
Placeholders:

- all placeholders from PlaceholderAPI

### playerCommand

Description: Changes the command that the player executes when interacting with the npc<br>
Syntax: ``/Npc playerCommand (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.playerCommand``<br>
Placeholders:

- all placeholders from PlaceholderAPI

### serverCommand

Description: Changes the command that the console executes when interacting with the npc<br>
Syntax: ``/Npc serverCommand (npc name) ('none' | message ...)``<br>
Permission: ``FancyNpcs.npc.serverCommand``<br>
Placeholders:

- all placeholders from PlaceholderAPI
- ``{player}`` - the player's username

### moveHere

Description: Teleports the npc to you<br>
Syntax: ``/Npc moveHere (npc name)``<br>
Permission: ``FancyNpcs.npc.moveHere``

