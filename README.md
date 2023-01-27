# NPC Plugin
_A simple NPC plugin for minecraft servers using [packets](https://wiki.vg/Protocol)_

**Only for minecraft server version 1.19.3**

## Commands

/npc create (name) - _Creates a new npc at your location_

/npc remove (name) - _Removes an npc_

/npc skin (name) (skin) - _Sets the skin for an npc_

/npc movehere (name) - _Teleports an npc to your location_

/npc displayName (name) (displayName ...) - _Sets the displayname for an npc_

/npc equipment (name) (slot) - _Equips the npc with the item you are holding_

/npc command (name) (command ...) - _The command will be executed when someone interacts with the npc_

/npc showInTab (name) (true|false) - _Whether the NPC will be shown in tab-list or not_

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