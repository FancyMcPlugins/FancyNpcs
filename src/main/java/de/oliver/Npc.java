package de.oliver;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import de.oliver.utils.ReflectionUtils;
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
    private String skin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private HashMap<EquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private ServerPlayer npc;

    public Npc(String name, String displayName, String skin, Location location, boolean showInTab, boolean spawnEntity, HashMap<EquipmentSlot, ItemStack> equipment) {
        this.name = name;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.spawnEntity = spawnEntity;
        this.equipment = equipment;
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
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        if(skin != null && skin.length() > 0) {
            // sessionserver.mojang.com/session/minecraft/profile/<UUID>?unsigned=false
//        String textureValue = "ewogICJ0aW1lc3RhbXAiIDogMTY3MTQ3MDEyMDYyMiwKICAicHJvZmlsZUlkIiA6ICI5YjYwNWQwNDVhNTk0MzUzYmJhMzJkZGY1NzBlYjM4YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPbGl2ZXJIRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iOWVhOGY1NjE3NjkwZWYyNzBkZjkwNWQ1M2RjOThiYWZhOWE1YmE0ODcxYWJhYWZjNTQ2ZDk0MTg3MmUzOWEiCiAgICB9CiAgfQp9";
//        String signature = "TlUD1fzHlHhS2GPK7qs3In798MU6HsOI+1Th7iFZ5ZAcDZtm4h1Eoce2Dh6pah9T8eSx7lQ9GsY0yw6zP9lCeeGZYIJ3BaGuhXWWUOOqH4CNGOKQ4MsANyCvIIArKOll0Uh4Es7+yI/AyXo3qNG2aNznP/vLACkUSz4/Bm5PdXkzlx8HjlH+NNWKiED52PqRXmqAS0NuCmDe/XhlI/r3oOanbkKLD8OhNBTXPNQ+lt8LZp1jumjpoBbpv28BYKK9lNCX5MQCItIeYEQZcmMJ8X23SHPteVZ/QtAx0lMkotwXDuQjbSi92aTyykc/5Z3oqUvoLG3Y4aC1UxNv1UtZNivM5Sk0qXmQCiv0xCzsFpLRT6zYSKGvFZwhSvVJ1uQ046Oy+zzGXi3zJ6GBM30KYH6Q6YYob7COUBe+KM3uLYBrTfHr4tOUV/W5T3cumsFCBJ/QS5K5XmjnlUX4A+XI6EYzvYsOewaKmL7rx7GKbwY3mS6RDgN82FJcTslZ/Jf85yVaLIRpDpX1nA/L1WQfVVWrghKG4h6Qs8zdhf2ftmvaXuozxKjxJUc6U2ExMvGDB9qiGAdm5sEGe+eVH7moIHrXH8gO7lwkJjhfTd4hn0jpp7gg6o4yNzWpRWDQ9M7FwItrfRC0209vAfwqTqLbqQD/6kn27ZskNada20gLYaY=";
//        gameProfile.getProperties().put("textures", new Property("textures", textureValue, signature));
        }

        npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile);
        npc.displayName = displayName;

        NpcPlugin.getInstance().getNpcManager().registerNpc(this);
    }

    public void spawn(Player target){
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

    public String getSkin() {
        return skin;
    }

    public Npc setSkin(String skin) {
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
        equipment.put(equipmentSlot, itemStack);
        return this;
    }

    public Npc setOnClick(Consumer<Player> consumer){
        onClick = consumer;
        return this;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public ServerPlayer getNpc() {
        return npc;
    }
}
