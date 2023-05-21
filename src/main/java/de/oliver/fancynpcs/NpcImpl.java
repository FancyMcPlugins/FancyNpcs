package de.oliver.fancynpcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.oliver.fancylib.RandomUtils;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import de.oliver.fancynpcs.utils.SkinFetcher;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class NpcImpl implements Npc {

    private static final char[] localNameChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

    private final String name;
    private final Map<UUID, Boolean> isTeamCreated = new HashMap<>();
    private final Map<UUID, Boolean> isVisibleForPlayer = new HashMap<>();
    private String displayName;
    private SkinFetcher skin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private boolean glowing;
    private ChatFormatting glowingColor;
    private EntityType<?> type;
    private Entity npc;
    private Map<EquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private boolean turnToPlayer;
    private String serverCommand;
    private String playerCommand;
    private String localName;
    private boolean isDirty;
    private boolean saveToFile;
    private String message;

    public NpcImpl(String name, EntityType<?> type, String displayName, SkinFetcher skin, Location location, boolean showInTab, boolean spawnEntity, boolean glow, ChatFormatting glowColor, Map<EquipmentSlot, ItemStack> equipment, Consumer<Player> onClick, boolean turnToPlayer, String serverCommand, String playerCommand, String message) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.glowing = glow;
        this.glowingColor = glowColor;
        this.spawnEntity = spawnEntity;
        this.equipment = equipment;
        this.onClick = onClick;
        this.turnToPlayer = turnToPlayer;
        this.serverCommand = serverCommand;
        this.playerCommand = playerCommand;
        this.isDirty = false;
        this.message = message;
        this.saveToFile = true;
        generateLocalName();
    }

    public NpcImpl(String name, Location location) {
        this.name = name;
        this.type = EntityType.PLAYER;
        this.displayName = name;
        this.location = location;
        this.showInTab = false;
        this.spawnEntity = true;
        this.glowing = false;
        this.glowingColor = ChatFormatting.WHITE;
        this.onClick = p -> {
        };
        this.turnToPlayer = false;
        this.isDirty = false;
        this.message = "";
        this.saveToFile = true;
        generateLocalName();
    }

    private void generateLocalName() {
        localName = "";
        for (int i = 0; i < 8; i++) {
            localName += "§" + localNameChars[(int) RandomUtils.randomInRange(0, localNameChars.length)];
        }
    }

    public void register() {
        FancyNpcs.getInstance().getNpcManager().registerNpc(this);
    }

    public void unregister() {
        FancyNpcs.getInstance().getNpcManager().removeNpc(this);
    }

    public void create() {
        MinecraftServer minecraftServer = FancyNpcs.getInstance().getNmsBase().getMinecraftServer(Bukkit.getServer());
        ServerLevel serverLevel = FancyNpcs.getInstance().getNmsBase().getServerLevel(location.getWorld());
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), localName);

        if (skin != null && skin.isLoaded()) {
            // sessionserver.mojang.com/session/minecraft/profile/<UUID>?unsigned=false
            gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        }

        if (type == EntityType.PLAYER) {
            npc = new ServerPlayer(minecraftServer, serverLevel, new GameProfile(gameProfile.getId(), ""));
            ((ServerPlayer) npc).gameProfile = gameProfile;
        } else {
            npc = type.create(serverLevel);
        }
    }

    private void spawn(ServerPlayer serverPlayer) {
        if (npc == null) {
            FancyNpcs.getInstance().getLogger().warning("Trying to spawn an NPC that was not created");
            return;
        }

        if (!location.getWorld().getName().equalsIgnoreCase(serverPlayer.getLevel().getWorld().getName())) {
            return;
        }

        List<Packet<ClientGamePacketListener>> packets = new ArrayList<>();

        Component vanillaComponent = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(displayName));
        if (!displayName.equalsIgnoreCase("<empty>")) {
            npc.setCustomName(vanillaComponent);
            npc.setCustomNameVisible(true);
        } else {
            npc.setCustomName(Component.empty());
            npc.setCustomNameVisible(false);
        }

        if (npc instanceof ServerPlayer player) {
            player.listName = vanillaComponent;

            EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
            if (showInTab) {
                actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);
            }

            ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, List.of(player));
            packets.add(playerInfoPacket);

            if (spawnEntity) {
                npc.setPos(location.x(), location.y(), location.z());
                ClientboundAddPlayerPacket spawnPlayerPacket = new ClientboundAddPlayerPacket(player);
                packets.add(spawnPlayerPacket);
            }

            // set custom name
            String teamName = "npc-" + localName;

            PlayerTeam team = new PlayerTeam(serverPlayer.getScoreboard(), teamName);
            team.setColor(glowingColor);
            if (displayName.equalsIgnoreCase("<empty>")) {
                team.setNameTagVisibility(Team.Visibility.NEVER);
            } else {
                team.setNameTagVisibility(Team.Visibility.ALWAYS);
            }
            team.getPlayers().clear();
            team.getPlayers().add(player.getGameProfile().getName());
            team.setPlayerPrefix(vanillaComponent);

            boolean isTeamCreatedForPlayer = isTeamCreated.getOrDefault(serverPlayer.getUUID(), false);

            ClientboundSetPlayerTeamPacket setPlayerTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, !isTeamCreatedForPlayer);
            packets.add(setPlayerTeamPacket);

            if (!isTeamCreatedForPlayer) {
                isTeamCreated.put(serverPlayer.getUUID(), true);
            }
        } else {
            ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(npc);
            packets.add(addEntityPacket);
        }


        if (npc instanceof ServerPlayer) {
            // Enable second layer of skin (https://wiki.vg/Entity_metadata#Player)
            npc.getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
        }

        npc.setGlowingTag(glowing);

        if (equipment != null && equipment.size() > 0) {
            List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();

            for (EquipmentSlot slot : equipment.keySet()) {
                equipmentList.add(new Pair<>(slot, equipment.get(slot)));
            }

            ClientboundSetEquipmentPacket setEquipmentPacket = new ClientboundSetEquipmentPacket(npc.getId(), equipmentList);
            packets.add(setEquipmentPacket);
        }

        ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(packets);
        serverPlayer.connection.send(bundlePacket);

        refreshEntityData(serverPlayer);

        if (spawnEntity && location != null) {
            move(serverPlayer, location);
        }

        isVisibleForPlayer.put(serverPlayer.getUUID(), true);
    }

    public void spawn(Player player) {
        NpcSpawnEvent npcSpawnEvent = new NpcSpawnEvent(this, player);
        npcSpawnEvent.callEvent();
        if (npcSpawnEvent.isCancelled()) return;

        
        ServerPlayer serverPlayer = FancyNpcs.getInstance().getNmsBase().getServerPlayer(player);
        spawn(serverPlayer);
    }

    public void spawnForAll() {
        // TODO: check for each player if NPC should be visible (see distance thing - PlayerMoveListener)
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }

    public void updateDisplayName(String displayName) {
        this.displayName = displayName;
        isDirty = true;

        Component vanillaComponent = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(displayName));
        if (displayName.equalsIgnoreCase("<empty>")) {
            npc.setCustomNameVisible(false);
            npc.setCustomName(Component.empty());
        } else {
            npc.setCustomNameVisible(true);
            npc.setCustomName(vanillaComponent);
        }

        if (npc instanceof ServerPlayer player) {
            player.listName = vanillaComponent;
        }

        removeForAll();
        create();
        spawnForAll();

    }

    public void updateSkin(SkinFetcher skin) {
        if (!skin.isLoaded()) {
            skin.load();
        }

        if (!skin.isLoaded()) {
            return;
        }

        this.skin = skin;
        isDirty = true;

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateGlowing(boolean glowing) {
        this.glowing = glowing;
        isDirty = true;

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateGlowingColor(ChatFormatting glowingColor) {
        this.glowingColor = glowingColor;
        isDirty = true;

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateShowInTab(boolean showInTab) {
        this.showInTab = showInTab;
        isDirty = true;

        if (!showInTab) {
            removeFromTabForAll();
        } else {
            removeForAll();
            create();
            spawnForAll();
        }
    }

    public void lookAt(ServerPlayer serverPlayer, Location location) {
        npc.setRot(location.getYaw(), location.getPitch());
        npc.setYHeadRot(location.getYaw());
        npc.setXRot(location.getPitch());
        npc.setYRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(npc);
        serverPlayer.connection.send(teleportEntityPacket);

        float angelMultiplier = 256f / 360f;
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(npc, (byte) (location.getYaw() * angelMultiplier));
        serverPlayer.connection.send(rotateHeadPacket);
    }

    @Override
    public void lookAt(Player player, Location location) {
        
        ServerPlayer serverPlayer = FancyNpcs.getInstance().getNmsBase().getServerPlayer(player);
        lookAt(serverPlayer, location);
    }

    private void move(ServerPlayer serverPlayer, Location location) {
        this.location = location;
        isDirty = true;

        npc.setPosRaw(location.x(), location.y(), location.z());
        npc.setRot(location.getYaw(), location.getPitch());
        npc.setYHeadRot(location.getYaw());
        npc.setXRot(location.getPitch());
        npc.setYRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(npc);
        ReflectionUtils.setValue(teleportEntityPacket, "b", location.x()); // 'x'
        ReflectionUtils.setValue(teleportEntityPacket, "c", location.y()); // 'y'
        ReflectionUtils.setValue(teleportEntityPacket, "d", location.z()); // 'z'
        serverPlayer.connection.send(teleportEntityPacket);

        float angelMultiplier = 256f / 360f;
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(npc, (byte) (location.getYaw() * angelMultiplier));
        serverPlayer.connection.send(rotateHeadPacket);
    }

    public void move(Player player, Location location) {
        
        ServerPlayer serverPlayer = FancyNpcs.getInstance().getNmsBase().getServerPlayer(player);
        move(serverPlayer, location);
    }

    public void moveForAll(Location location) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            move(onlinePlayer, location);
        }
    }

    private void remove(ServerPlayer serverPlayer) {
        if (showInTab) {
            removeFromTab(serverPlayer);
        }

        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(npc.getId());
        serverPlayer.connection.send(removeEntitiesPacket);

        isVisibleForPlayer.put(serverPlayer.getUUID(), false);
    }

    public void remove(Player player) {
        
        ServerPlayer serverPlayer = FancyNpcs.getInstance().getNmsBase().getServerPlayer(player);
        remove(serverPlayer);
    }

    public void removeForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            remove(onlinePlayer);
        }
    }

    private void removeFromTab(ServerPlayer serverPlayer) {
        if (!(npc instanceof ServerPlayer player)) {
            return;
        }

        ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, player);

        ClientboundPlayerInfoUpdatePacket.Entry entry = playerInfoUpdatePacket.entries().get(0);
        ClientboundPlayerInfoUpdatePacket.Entry newEntry = new ClientboundPlayerInfoUpdatePacket.Entry(
                entry.profileId(),
                entry.profile(),
                false,
                entry.latency(),
                entry.gameMode(),
                entry.displayName(),
                entry.chatSession()
        );

        // replace the old entry with the new entry
        ReflectionUtils.setValue(playerInfoUpdatePacket, "b", List.of(newEntry)); // 'entries'

        serverPlayer.connection.send(playerInfoUpdatePacket);
    }

    private void removeFromTab(Player player) {
        
        ServerPlayer serverPlayer = FancyNpcs.getInstance().getNmsBase().getServerPlayer(player);
        removeFromTab(serverPlayer);
    }

    private void removeFromTabForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            removeFromTab(onlinePlayer);
        }
    }

    private void refreshEntityData(ServerPlayer serverPlayer) {
        Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) ReflectionUtils.getValue(npc.getEntityData(), "e"); // itemsById
        List<SynchedEntityData.DataValue<?>> entityData = new ArrayList<>();
        for (SynchedEntityData.DataItem<?> dataItem : itemsById.values()) {
            entityData.add(dataItem.value());
        }
        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(npc.getId(), entityData);
        serverPlayer.connection.send(setEntityDataPacket);
    }

    public String getName() {
        return name;
    }

    public EntityType<?> getType() {
        return type;
    }

    public void setType(EntityType<?> type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        isDirty = true;
    }

    public SkinFetcher getSkin() {
        return skin;
    }

    public void setSkin(SkinFetcher skin) {
        this.skin = skin;
        isDirty = true;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        isDirty = true;
    }

    public boolean isShowInTab() {
        return showInTab;
    }

    public void setShowInTab(boolean showInTab) {
        this.showInTab = showInTab;
        isDirty = true;
    }

    public boolean isSpawnEntity() {
        return spawnEntity;
    }

    public void setSpawnEntity(boolean spawnEntity) {
        this.spawnEntity = spawnEntity;
        isDirty = true;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        isDirty = true;
    }

    public ChatFormatting getGlowingColor() {
        return glowingColor;
    }

    public void setGlowingColor(ChatFormatting glowingColor) {
        this.glowingColor = glowingColor;
        isDirty = true;
    }

    public void addEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        if (equipment == null) {
            equipment = new HashMap<>();
        }

        equipment.put(equipmentSlot, itemStack);
        isDirty = true;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public void setOnClick(Consumer<Player> consumer) {
        onClick = consumer;
    }

    public boolean isTurnToPlayer() {
        return turnToPlayer;
    }

    public void setTurnToPlayer(boolean turnToPlayer) {
        this.turnToPlayer = turnToPlayer;
        isDirty = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.isDirty = true;
    }

    public String getServerCommand() {
        return serverCommand;
    }

    public void setServerCommand(String serverCommand) {
        this.serverCommand = serverCommand;
        isDirty = true;
    }

    public String getPlayerCommand() {
        return playerCommand;
    }

    public void setPlayerCommand(String playerCommand) {
        this.playerCommand = playerCommand;
        isDirty = true;
    }

    public Entity getNpc() {
        return npc;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
    }

    public Map<UUID, Boolean> getIsTeamCreated() {
        return isTeamCreated;
    }

    public Map<UUID, Boolean> getIsVisibleForPlayer() {
        return isVisibleForPlayer;
    }
}
