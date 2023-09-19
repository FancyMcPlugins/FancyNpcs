![](fancynpcs_title.png)

#                       

![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyNpcs?style=flat-square)
[![Generic badge](https://img.shields.io/badge/folia-supported-green.svg)](https://shields.io/)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyNpcs/total?logo=GitHub&style=flat-square)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-107306.svg)](https://www.spigotmc.org/resources/npc-plugin-1-19-4.107306/)
[![Downloads](https://img.shields.io/modrinth/dt/fancynpcs?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancynpcs/versions)

Simple, lightweight and fast NPC plugin using [packets](https://wiki.vg/Protocol)

**Only for minecraft server version 1.19.4 - 1.20.2**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin

### Stable versions

- https://hangar.papermc.io/Oliver/FancyNpcs
- https://modrinth.com/plugin/fancynpcs/
- https://github.com/FancyMcPlugins/FancyNpcs/releases
- https://www.spigotmc.org/resources/fancy-npcs-1-20.107306/

### Development builds

- https://fancyplugins.de/fancynpcs/download/
- [Build from source](Build%20from%20source)

# Documentation

The official documentation is being moved from here to the official website.

Link: https://fancyplugins.de/fancynpcs/docs/

The content below might be out-dated, please use the website above.

## Commands

/npc create (name) - _Creates a new npc at your location_<br>
/npc remove (name) - _Removes an npc_<br>
/npc copy (name) (new name) - _Copies an npc_<br>
/npc list - _Summary of all npcs_<br>
/npc skin (name) [(skin)] - _Sets the skin for an npc_<br>
/npc type (name) (type) - _Sets the entity type for an npc_<br>
/npc attribute (name) (attribute) (value)- _Set certain npc attributes_<br>
/npc movehere (name) - _Teleports an npc to your location_<br>
/npc displayName (name) (displayName ...) - _Sets the displayname for an npc_<br>
/npc equipment (name) (slot) - _Equips the npc with the item you are holding_<br>
/npc message (name) ('none'|message) - _Set NPC message_<br>
/npc serverCommand (name) ('none'|command ...) - _The command will be executed by the console when someone interacts
with the
npc_<br>
/npc playerCommand (name) ('none'|command ...) - _Executes the command on a player when interacting_<br>
/npc showInTab (name) (true|false) - _Whether the NPC will be shown in tab-list or not_<br>
/npc turnToPlay (name) (true|false) - _Whether the NPC will turn to you or not_<br>
/npc glowing (name) (true|false) - _Whether the NPC will glow or not_<br>
/npc glowingColor (name) (color) - _Changes the color of the glowing effect_<br>
/fancynpcs version - _Shows you the current plugin version_<br>
/fancynpcs save - _Saves all npcs_<br>
/fancynpcs reload - _Reloads the config and npcs_
<br>
<br>
Using `<empty>` as displayName will make the whole name tag invisible.

For the serverCommand, there is a placeholder `{player}` - it will be replaced with the username of the player who
interacted with the npc.

You can also use any placeholder from PlaceholderAPI.

## Permissions

For the /fancynpcs command - ``FancyNpcs.admin``<br>
For the /npc command - ``FancyNpcs.npc``<br>
For all subcommands of /npc - ``FancyNpcs.npc.*``<br>
Permission for a subcommand of /npc - ``FancyNpcs.npc.<subcommand>``

## Entity types

When using any entity type other than Player, the following features are disabled:

- Changing the skin
- Changing the equipment
- Showing in tab

## Multiple lines

To have multiple lines as the display name of an NPC follow the following steps:

1. download the [FancyHolograms](https://modrinth.com/plugin/fancyholograms/versions) plugin and put it in the plugins
   folder
2. start your server and make sure the FancyNpcs and FancyHolograms plugin have both loaded
3. create a hologram with multiple lines
4. create a npc
5. link the hologram with the npc `/hologram edit <hologram> linkWithNpc <npc>`
6. you can now move the npc around and the hologram will always follow
7. to unlink simply run `/hologram edit <hologram> unlinkWithNpc`

## Build from source

1. Clone this repo and run `gradlew shadowJar`
2. The jar file will be in `build/libs/FancyNpcs-<version>.jar`

## Examples

![](exampleImages/example1.png)

![](exampleImages/example3.png)

![](exampleImages/example2.png)

