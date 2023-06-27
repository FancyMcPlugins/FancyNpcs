# API usage

## Using as dependency

Include the following in your plugin's gradle project:

```gradle
repositories {
    maven("https://repo.fancyplugins.de/releases")
    ...
}

dependencies {
    implementation("de.oliver:FancyNpcs:version")
    ...
}
```

You find the current version in the `README.md` file.

## Creating and modifying NPCs

### Getting an existing NPC

```java
Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(npcName);
```

### Creating a NPC

```java
Npc npc = new Npc(npcName, location);
npc.create();
npc.spawnForAll();
```

### Removing

```java
npc.removeForAll();
```

### Moving

```java
npc.moveForAll(location);
```

### Changing skin

```java

SkinFetcher skin = new SkinFetcher(UUIDFetcher.getUUID(playerName).toString());
npc.updateSkin(skin);
```

### Display name

```java
npc.updateDisplayName(displayName);
```

### Equipment

```java
npc.addEquipment(equipmentSlot, CraftItemStack.asNMSCopy(item));
npc.removeForAll();
npc.create();
npc.spawnForAll();
```

### Server command

```java
npc.setServerCommand(command);
```

### Server command

```java
npc.setPlayerCommand(command);
```

### Show in tab

```java
npc.updateShowInTab(shouldShowInTab);
```

### Glowing

```java
npc.updateGlowing(shouldGlow);
```

### Glowing color

```java
ChatFormatting color = ChatFormatting.getByName("color name");
npc.updateGlowingColor(color);
```

```java
ChatFormatting color = ChatFormatting.getByName("color name");
npc.updateGlowingColor(ChatFormatting.RED);
```

### Turn to player

```java
npc.setTurnToPlayer(shouldTurnToPlayer);
npc.moveForAll(npc.getLocation()); // initially refreshing (optional)
```

### Custom interact

```java
npc.setOnClick(player -> {
    // do something with the player
});
```

## Events

### NpcCreateEvent

Is fired when a new NPC is being created.<br>
Contains the player who created the NPC and the NPC object.

### NpcRemoveEvent

Is fired when a NPC is being deleted.<br>
Contains the player who removed the NPC and the NPC object.

### NpcModifyEvent

Is fired when a NPC is being modified.<br>
Contains the player who modified the NPC, the modification and the NPC object.

### NpcInteractEvent

Is fired when a player interacts with a NPC.<br>
Contains the player who interacted, the NPC and all actions.

### NpcSpawnEvent

Is fired when a NPC is being spawned. This can happen when a player joins, a player switches the world or the NPC is
being modified.<br>
Contains the NPC that is being spawned and the player to whom the spawn packets are being sent.