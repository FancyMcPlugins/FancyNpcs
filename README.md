[![Generic badge](https://img.shields.io/badge/version-1.1.1-green.svg)](https://shields.io/)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-107306.svg)](https://www.spigotmc.org/resources/npc-plugin-1-19-4.107306/)
[![Downloads](https://img.shields.io/modrinth/dt/npc-plugin?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/npc-plugin/versions)

# Fancy Npcs
A simple NPC plugin for minecraft servers using [packets](https://wiki.vg/Protocol)

**Only for minecraft server version 1.19.4**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin
You can download the latest versions at the following places:

- https://www.spigotmc.org/resources/npc-plugin-1-19-4.107306/
- https://modrinth.com/plugin/fancy-npcs/
- https://github.com/OliverSchlueter/FancyNpcs/releases
- Build from source (``gradlew reobfJar``)

## Commands
/npc create (name) - _Creates a new npc at your location_<br>
/npc remove (name) - _Removes an npc_<br>
/npc list - _Summary of all npcs_<br>
/npc skin (name) (skin) - _Sets the skin for an npc_<br>
/npc movehere (name) - _Teleports an npc to your location_<br>
/npc displayName (name) (displayName ...) - _Sets the displayname for an npc_<br>
/npc equipment (name) (slot) - _Equips the npc with the item you are holding_<br>
/npc serverCommand (name) (command ...) - _The command will be executed by the console when someone interacts with the npc_<br>
/npc playerCommand (name) (command ...) - _Executes the command on a player when interacting_<br>
/npc showInTab (name) (true|false) - _Whether the NPC will be shown in tab-list or not_<br>
/npc turnToPlay (name) (true|false) - _Whether the NPC will turn to you or not_<br>
<br>
<br>
Using `<empty>` as displayName will make the whole name tag invisible.

For the serverCommand, there is a placeholder `{player}` - it will be replaced with the username of the player who interacted with the npc.

## Permissions
For the /npc command - ``FancyNpcs.admin``

## Used packets

_(Just a note for me when updating this plugin to a new version)_

- ClientboundPlayerInfoUpdatePacket
- ClientboundAddPlayerPacket
- ClientboundSetPlayerTeamPacket
- ClientboundSetEntityDataPacket
- ClientboundSetEquipmentPacket
- ClientboundTeleportEntityPacket
- ClientboundRotateHeadPacket
- ClientboundRemoveEntitiesPacket

### Mappings

- Npc#move
  - ClientboundTeleportEntityPacket.b ('x')
  - ClientboundTeleportEntityPacket.c ('y')
  - ClientboundTeleportEntityPacket.d ('z')

- Npc#removeFromTab
  - ClientboundPlayerInfoUpdatePacket.b ('entries')