# NPC Plugin
A simple NPC plugin for minecraft servers using [packets](https://wiki.vg/Protocol)

**Only for minecraft server version 1.19.3**<br>
_Using paper is recommended_

## Commands
/npc create (name) - _Creates a new npc at your location_<br>
/npc remove (name) - _Removes an npc_<br>
/npc skin (name) (skin) - _Sets the skin for an npc_<br>
/npc movehere (name) - _Teleports an npc to your location_<br>
/npc displayName (name) (displayName ...) - _Sets the displayname for an npc_<br>
/npc equipment (name) (slot) - _Equips the npc with the item you are holding_<br>
/npc serverCommand (name) (command ...) - _The command will be executed by the console when someone interacts with the npc_<br>
/npc playerCommand (name) (command ...) - _Executes the command on a player when interacting_<br>
/npc showInTab (name) (true|false) - _Whether the NPC will be shown in tab-list or not_<br>

For the serverCommand, there is a placeholder `{player}` - it will be replaced with the username of the player who interacted with the npc.

## Permissions
For the /npc command - ``NpcPlugin.admin``

## Used packets

_(Just a note for me when updating this plugin to a new version)_

- ClientboundPlayerInfoUpdatePacket
- ClientboundAddPlayerPacket
- ClientboundSetEntityDataPacket
- ClientboundSetEquipmentPacket
- ClientboundTeleportEntityPacket
- ClientboundRotateHeadPacket
- ClientboundRemoveEntitiesPacket

### Mappings

- Npc#move
  - ClientboundTeleportEntityPacket.b
  - ClientboundTeleportEntityPacket.c
  - ClientboundTeleportEntityPacket.d

- Npc#removeFromTab
  - ClientboundPlayerInfoUpdatePacket.b

### Version changes

- 23w03a
  - no relevant changes for this plugin
- 23w04a
  - no relevant changes for this plugin
- 23w05a
  - no relevant changes for this plugin
- 23w06a
  - no relevant changes for this plugin
- 23w07a
  - no relevant changes for this plugin
- 1.19.4-pre1
  - no relevant changes for this plugin
- 1.19.4-pre2
  - no relevant changes for this plugin
- 1.19.4-pre3
  - no relevant changes for this plugin
- 1.19.4-pre4
  - no relevant changes for this plugin