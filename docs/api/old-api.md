# API usage (might be outdated!)

## Creating and modifying NPCs

### Getting an existing NPC

```java
Npc npc=FancyNpcs.getInstance().getNpcManager().getNpc(npcName);
```

### Creating a NPC

```java
Npc npc=new Npc(npcName,location);
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

SkinFetcher skin=new SkinFetcher(UUIDFetcher.getUUID(playerName).toString());
        npc.updateSkin(skin);
```

### Display name

```java
npc.updateDisplayName(displayName);
```

### Equipment

```java
npc.addEquipment(equipmentSlot,CraftItemStack.asNMSCopy(item));
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
ChatFormatting color=ChatFormatting.getByName("color name");
        npc.updateGlowingColor(color);
```

```java
ChatFormatting color=ChatFormatting.getByName("color name");
        npc.updateGlowingColor(ChatFormatting.RED);
```

### Turn to player

```java
npc.setTurnToPlayer(shouldTurnToPlayer);
        npc.moveForAll(npc.getLocation()); // initially refreshing (optional)
```

### Custom interact

```java
npc.setOnClick(player->{
        // do something with the player
        });
```