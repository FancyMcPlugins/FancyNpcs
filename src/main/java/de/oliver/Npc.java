package de.oliver;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.oliver.utils.ReflectionUtils;
import de.oliver.utils.SkinFetcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class Npc {

    private String name;
    private String displayName;
    private SkinFetcher skin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private Map<EquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private String command;
    private ServerPlayer npc;

    public Npc(String name, String displayName, SkinFetcher skin, Location location, boolean showInTab, boolean spawnEntity, Map<EquipmentSlot, ItemStack> equipment, Consumer<Player> onClick, String command) {
        this.name = name;
        this.displayName = displayName;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.spawnEntity = spawnEntity;
        this.equipment = equipment;
        this.onClick = onClick;
        this.command = command;
    }

    public Npc(String name, Location location){
        this.name = name;
        this.displayName = name;
        this.location = location;
        this.showInTab = false;
        this.spawnEntity = true;
        this.onClick = p -> {};
    }

    public void create(){
        if(NpcPlugin.getInstance().getNpcManager().getNpc(name) != null){
            NpcPlugin.getInstance().getNpcManager().removeNpc(this);
        }

        MinecraftServer minecraftServer = ((CraftServer)Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld)location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), displayName);

        if(skin != null && skin.isLoaded()) {
            // sessionserver.mojang.com/session/minecraft/profile/<UUID>?unsigned=false
            gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        }

        npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile);
        npc.displayName = displayName;

        NpcPlugin.getInstance().getNpcManager().registerNpc(this);
    }

    public void spawn(Player target){
        if(!location.getWorld().getName().equalsIgnoreCase(target.getWorld().getName())){
            return;
        }

        CraftPlayer craftPlayer = (CraftPlayer) target;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        if(showInTab){
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        }

        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, List.of(npc));
        serverPlayer.connection.send(playerInfoPacket);

        if(spawnEntity) {
            npc.setPos(location.x(), location.y(), location.z());
            ClientboundAddPlayerPacket spawnPlayerPacket = new ClientboundAddPlayerPacket(npc);
            serverPlayer.connection.send(spawnPlayerPacket);

            if(location != null) {
                move(serverPlayer, location);
            }
        }

        // Enable second layer of skin (https://wiki.vg/Entity_metadata#Player)
        npc.getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));

        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData().getNonDefaultValues());
        serverPlayer.connection.send(setEntityDataPacket);

        if(equipment != null && equipment.size() > 0) {
            List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();

            for (EquipmentSlot slot : equipment.keySet()) {
                equipmentList.add(new Pair<>(slot, equipment.get(slot)));
            }

            ClientboundSetEquipmentPacket setEquipmentPacket = new ClientboundSetEquipmentPacket(npc.getId(), equipmentList);
            serverPlayer.connection.send(setEquipmentPacket);
        }
    }

    public void spawnForAll(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }

    public void updateDisplayName(String displayName){
        this.displayName = displayName;
        npc.listName = Component.literal(displayName);

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateSkin(SkinFetcher skin){
        if(!skin.isLoaded()){
            return;
        }

        this.skin = skin;

        removeForAll();
        create();
        spawnForAll();
    }

    public void move(ServerPlayer serverPlayer, Location location){
        this.location = location;

        npc.setPosRaw(location.x(), location.y(), location.z());
        npc.setRot(location.getYaw(), location.getPitch());
        npc.setYHeadRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(npc);
        ReflectionUtils.setValue(teleportEntityPacket, "b", location.x());
        ReflectionUtils.setValue(teleportEntityPacket, "c", location.y());
        ReflectionUtils.setValue(teleportEntityPacket, "d", location.z());
        serverPlayer.connection.send(teleportEntityPacket);

        float angelMultiplier = 256f / 360f;
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(npc, (byte)(location.getYaw()*angelMultiplier));
        serverPlayer.connection.send(rotateHeadPacket);
    }

    public void move(Player player, Location location){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        move(serverPlayer, location);
    }

    public void moveForAll(Location location){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            move(onlinePlayer, location);
        }
    }

    public void remove(ServerPlayer serverPlayer){
        NpcPlugin.getInstance().getNpcManager().removeNpc(this);

        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(npc.getId());
        serverPlayer.connection.send(removeEntitiesPacket);
    }

    public void remove(Player player){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        remove(serverPlayer);
    }

    public void removeForAll(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            remove(onlinePlayer);
        }
    }

    public String getName() {
        return name;
    }

    public Npc setName(String name) {
        this.name = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Npc setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SkinFetcher getSkin() {
        return skin;
    }

    public Npc setSkin(SkinFetcher skin) {
        this.skin = skin;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public Npc setLocation(Location location) {
        this.location = location;
        return this;
    }

    public boolean isShowInTab() {
        return showInTab;
    }

    public void setShowInTab(boolean showInTab) {
        this.showInTab = showInTab;
    }

    public boolean isSpawnEntity() {
        return spawnEntity;
    }

    public Npc setSpawnEntity(boolean spawnEntity) {
        this.spawnEntity = spawnEntity;
        return this;
    }

    public Npc addEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack){
        if(equipment == null){
            equipment = new HashMap<>();
        }

        equipment.put(equipmentSlot, itemStack);
        return this;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }

    public Npc setOnClick(Consumer<Player> consumer){
        onClick = consumer;
        return this;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public String getCommand() {
        return command;
    }

    public Npc setCommand(String command) {
        this.command = command;
        return this;
    }

    public ServerPlayer getNpc() {
        return npc;
    }
}
