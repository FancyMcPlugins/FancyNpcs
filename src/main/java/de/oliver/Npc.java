package de.oliver;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.oliver.utils.RandomUtils;
import de.oliver.utils.ReflectionUtils;
import de.oliver.utils.SkinFetcher;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class Npc {

    private static final char[] localNameChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r' };

    private final String name;
    private String displayName;
    private SkinFetcher skin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private boolean glowing;
    private ChatFormatting glowingColor;
    private ServerPlayer npc;
    private Map<EquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private boolean turnToPlayer;
    private String serverCommand;
    private String playerCommand;
    private String localName;
    private final Map<UUID, Boolean> isTeamCreated = new HashMap<>();

    public Npc(String name, String displayName, SkinFetcher skin, Location location, boolean showInTab, boolean spawnEntity, boolean glow, ChatFormatting glowColor, Map<EquipmentSlot, ItemStack> equipment, Consumer<Player> onClick, boolean turnToPlayer, String serverCommand, String playerCommand) {
        this.name = name;
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
        generateLocalName();
    }

    public Npc(String name, Location location){
        this.name = name;
        this.displayName = name;
        this.location = location;
        this.showInTab = false;
        this.spawnEntity = true;
        this.glowing = false;
        this.glowingColor = ChatFormatting.WHITE;
        this.onClick = p -> {};
        this.turnToPlayer = false;
        generateLocalName();
    }

    private void generateLocalName(){
        localName = "";
        for (int i = 0; i < 8; i++) {
            localName += "§" + localNameChars[(int) RandomUtils.randomInRange(0, localNameChars.length)];
        }
    }

    public void create(){
        if(NpcPlugin.getInstance().getNpcManager().getNpc(name) != null){
            NpcPlugin.getInstance().getNpcManager().removeNpc(this);
        }

        MinecraftServer minecraftServer = ((CraftServer)Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld)location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), localName);

        if(skin != null && skin.isLoaded()) {
            // sessionserver.mojang.com/session/minecraft/profile/<UUID>?unsigned=false
            gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        }

        npc = new ServerPlayer(minecraftServer, serverLevel, new GameProfile(gameProfile.getId(), ""));
        npc.gameProfile = gameProfile;

        NpcPlugin.getInstance().getNpcManager().registerNpc(this);
    }

    private void spawn(ServerPlayer serverPlayer){
        if(!location.getWorld().getName().equalsIgnoreCase(serverPlayer.getLevel().getWorld().getName())){
            return;
        }

        npc.displayName = displayName;
        npc.listName = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(displayName));

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        if(showInTab){
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);
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

        // set custom name
        String teamName = "npc-" + localName;

        PlayerTeam team = new PlayerTeam(serverPlayer.getScoreboard(), teamName);
        team.setColor(glowingColor);
        if(displayName.equalsIgnoreCase("<empty>")){
            team.setNameTagVisibility(Team.Visibility.NEVER);
        } else {
            team.setNameTagVisibility(Team.Visibility.ALWAYS);
        }
        team.getPlayers().clear();
        team.getPlayers().add(npc.getGameProfile().getName());
        team.setPlayerPrefix(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(displayName)));

        boolean isTeamCreatedForPlayer = isTeamCreated.getOrDefault(serverPlayer.getUUID(), false);

        ClientboundSetPlayerTeamPacket setPlayerTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, !isTeamCreatedForPlayer);
        serverPlayer.connection.send(setPlayerTeamPacket);

        if(!isTeamCreatedForPlayer){
            isTeamCreated.put(serverPlayer.getUUID(), true);
        }


        // Enable second layer of skin (https://wiki.vg/Entity_metadata#Player)
        npc.getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
        npc.setGlowingTag(glowing);

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

    public void spawn(Player player){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        spawn(serverPlayer);
    }

    public void spawnForAll(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }

    public void updateDisplayName(String displayName){
        this.displayName = displayName;
        npc.listName = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(displayName));
        npc.displayName = displayName;

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

    public void updateGlowing(boolean glowing){
        this.glowing = glowing;

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateGlowingColor(ChatFormatting glowingColor){
        this.glowingColor = glowingColor;

        removeForAll();
        create();
        spawnForAll();
    }

    public void updateShowInTab(boolean showInTab){
        this.showInTab = showInTab;

        if(!showInTab){
            removeFromTabForAll();
        } else {
            removeForAll();
            create();
            spawnForAll();
        }
    }

    public void lookAt(ServerPlayer serverPlayer, Location location){
        npc.setRot(location.getYaw(), location.getPitch());
        npc.setYHeadRot(location.getYaw());
        npc.setXRot(location.getPitch());
        npc.setYRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(npc);
        serverPlayer.connection.send(teleportEntityPacket);

        float angelMultiplier = 256f / 360f;
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(npc, (byte)(location.getYaw()*angelMultiplier));
        serverPlayer.connection.send(rotateHeadPacket);
    }

    private void move(ServerPlayer serverPlayer, Location location){
        this.location = location;

        npc.setPosRaw(location.x(), location.y(), location.z());
        npc.setRot(location.getYaw(), location.getPitch());
        npc.setYHeadRot(location.getYaw());
        npc.setXRot(location.getPitch());
        npc.setYRot(location.getYaw());

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

    private void remove(ServerPlayer serverPlayer){
        NpcPlugin.getInstance().getNpcManager().removeNpc(this);

        if(showInTab){
            removeFromTab(serverPlayer);
        }

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

    private void removeFromTab(ServerPlayer serverPlayer){
        ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, npc);

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
        ReflectionUtils.setValue(playerInfoUpdatePacket, "b", List.of(newEntry));

        serverPlayer.connection.send(playerInfoUpdatePacket);
    }

    private void removeFromTab(Player player){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        removeFromTab(serverPlayer);
    }

    private void removeFromTabForAll(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            removeFromTab(onlinePlayer);
        }
    }

    public String getName() {
        return name;
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

    public Npc setShowInTab(boolean showInTab) {
        this.showInTab = showInTab;
        return this;
    }

    public boolean isSpawnEntity() {
        return spawnEntity;
    }

    public Npc setSpawnEntity(boolean spawnEntity) {
        this.spawnEntity = spawnEntity;
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public Npc setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ChatFormatting getGlowingColor() {
        return glowingColor;
    }

    public Npc setGlowingColor(ChatFormatting glowingColor) {
        this.glowingColor = glowingColor;
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

    public boolean isTurnToPlayer() {
        return turnToPlayer;
    }

    public void setTurnToPlayer(boolean turnToPlayer) {
        this.turnToPlayer = turnToPlayer;
    }

    public String getServerCommand() {
        return serverCommand;
    }

    public Npc setServerCommand(String serverCommand) {
        this.serverCommand = serverCommand;
        return this;
    }

    public String getPlayerCommand() {
        return playerCommand;
    }

    public Npc setPlayerCommand(String playerCommand) {
        this.playerCommand = playerCommand;
        return this;
    }

    public ServerPlayer getNpc() {
        return npc;
    }

    public Map<UUID, Boolean> getIsTeamCreated() {
        return isTeamCreated;
    }
}
