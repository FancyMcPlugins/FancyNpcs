package de.oliver.fancynpcs.api;

import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class NpcData {

    private final String id;
    private final String name;
    private final UUID creator;
    private String displayName;
    private SkinFetcher skin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private boolean collidable;
    private boolean glowing;
    private NamedTextColor glowingColor;
    private EntityType type;
    private Map<NpcEquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private boolean turnToPlayer;
    private List<String> playerCommands;
    private List<String> serverCommands;
    private List<String> messages;
    private boolean sendMessagesRandomly;
    private float interactionCooldown;
    private float scale;
    private Map<NpcAttribute, String> attributes;
    private boolean isDirty;
    private boolean mirrorSkin;

    public NpcData(
            String id,
            String name,
            UUID creator,
            String displayName,
            SkinFetcher skin,
            Location location,
            boolean showInTab,
            boolean spawnEntity,
            boolean collidable,
            boolean glowing,
            NamedTextColor glowingColor,
            EntityType type,
            Map<NpcEquipmentSlot, ItemStack> equipment,
            boolean turnToPlayer,
            Consumer<Player> onClick,
            List<String> messages,
            boolean sendMessagesRandomly,
            List<String> serverCommands,
            List<String> playerCommands,
            float interactionCooldown,
            float scale,
            Map<NpcAttribute, String> attributes,
            boolean mirrorSkin
    ) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.displayName = displayName;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.spawnEntity = spawnEntity;
        this.collidable = collidable;
        this.glowing = glowing;
        this.glowingColor = glowingColor;
        this.type = type;
        this.equipment = equipment;
        this.onClick = onClick;
        this.turnToPlayer = turnToPlayer;
        this.serverCommands = serverCommands;
        this.playerCommands = playerCommands;
        this.messages = messages;
        this.sendMessagesRandomly = sendMessagesRandomly;
        this.interactionCooldown = interactionCooldown;
        this.scale = scale;
        this.attributes = attributes;
        this.mirrorSkin = mirrorSkin;
        this.isDirty = true;
    }

    /**
     * Creates a default npc with random id
     */
    public NpcData(String name, UUID creator, Location location) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.creator = creator;
        this.location = location;
        this.displayName = name;
        this.type = EntityType.PLAYER;
        this.showInTab = false;
        this.spawnEntity = true;
        this.collidable = true;
        this.glowing = false;
        this.glowingColor = NamedTextColor.WHITE;
        this.onClick = p -> {
        };
        this.turnToPlayer = false;
        this.messages = new ArrayList<>();
        this.serverCommands = new ArrayList<>();
        this.playerCommands = new ArrayList<>();
        this.sendMessagesRandomly = false;
        this.interactionCooldown = 0;
        this.scale = 1;
        this.equipment = new HashMap<>();
        this.attributes = new HashMap<>();
        this.mirrorSkin = false;
        this.isDirty = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getCreator() {
        return creator == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") : creator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NpcData setDisplayName(String displayName) {
        this.displayName = displayName;
        isDirty = true;
        return this;
    }

    public SkinFetcher getSkin() {
        return skin;
    }

    public NpcData setSkin(SkinFetcher skin) {
        this.skin = skin;
        isDirty = true;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public NpcData setLocation(Location location) {
        this.location = location;
        isDirty = true;
        return this;
    }

    public boolean isShowInTab() {
        return showInTab;
    }

    public NpcData setShowInTab(boolean showInTab) {
        this.showInTab = showInTab;
        isDirty = true;
        return this;
    }

    public boolean isSpawnEntity() {
        return spawnEntity;
    }

    public NpcData setSpawnEntity(boolean spawnEntity) {
        this.spawnEntity = spawnEntity;
        isDirty = true;
        return this;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public NpcData setCollidable(boolean collidable) {
        this.collidable = collidable;
        isDirty = true;
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public NpcData setGlowing(boolean glowing) {
        this.glowing = glowing;
        isDirty = true;
        return this;
    }

    public NamedTextColor getGlowingColor() {
        return glowingColor;
    }

    public NpcData setGlowingColor(NamedTextColor glowingColor) {
        this.glowingColor = glowingColor;
        isDirty = true;
        return this;
    }

    public EntityType getType() {
        return type;
    }

    public NpcData setType(EntityType type) {
        this.type = type;
        attributes.clear();
        isDirty = true;
        return this;
    }

    public Map<NpcEquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }

    public NpcData setEquipment(Map<NpcEquipmentSlot, ItemStack> equipment) {
        this.equipment = equipment;
        isDirty = true;
        return this;
    }

    public NpcData addEquipment(NpcEquipmentSlot slot, ItemStack item) {
        equipment.put(slot, item);
        isDirty = true;
        return this;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public NpcData setOnClick(Consumer<Player> onClick) {
        this.onClick = onClick;
        isDirty = true;
        return this;
    }

    public boolean isTurnToPlayer() {
        return turnToPlayer;
    }

    public NpcData setTurnToPlayer(boolean turnToPlayer) {
        this.turnToPlayer = turnToPlayer;
        isDirty = true;
        return this;
    }

    public List<String> getServerCommands() {
        return serverCommands;
    }

    public NpcData setServerCommands(List<String> serverCommands) {
        this.serverCommands = serverCommands;
        isDirty = true;
        return this;
    }

    public void addServerCommand(String command) {
        serverCommands.add(command);
        isDirty = true;
    }

    public void removeServerCommand(int index) {
        serverCommands.remove(index);
        isDirty = true;
    }

    public List<String> getPlayerCommands() {
        return playerCommands;
    }

    public NpcData setPlayerCommands(List<String> playerCommands) {
        this.playerCommands = playerCommands;
        isDirty = true;
        return this;
    }

    public List<String> getMessages() {
        return messages;
    }

    public NpcData setMessages(List<String> messages) {
        this.messages = messages;
        return this;
    }

    public void addPlayerCommand(String command) {
        playerCommands.add(command);
        isDirty = true;
    }

    public void removePlayerCommand(int index) {
        playerCommands.remove(index);
        isDirty = true;
    }

    public boolean isSendMessagesRandomly() {
        return sendMessagesRandomly;
    }

    public void setSendMessagesRandomly(boolean sendMessagesRandomly) {
        this.sendMessagesRandomly = sendMessagesRandomly;
    }

    public void addMessage(String message) {
        messages.add(message);
        isDirty = true;
    }

    public void removeMessage(int index) {
        messages.remove(index);
        isDirty = true;
    }

    public float getInteractionCooldown() {
        return interactionCooldown;
    }

    public NpcData setInteractionCooldown(float interactionCooldown) {
        this.interactionCooldown = interactionCooldown;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public NpcData setScale(float scale) {
        this.scale = scale;
        isDirty = true;
        return this;
    }

    public Map<NpcAttribute, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(NpcAttribute attribute, String value) {
        attributes.put(attribute, value);
        isDirty = true;
    }

    public void applyAllAttributes(Npc npc) {
        for (NpcAttribute attribute : attributes.keySet()) {
            attribute.apply(npc, attributes.get(attribute));
        }
    }

    public boolean isMirrorSkin() {
        return mirrorSkin;
    }

    public NpcData setMirrorSkin(boolean mirrorSkin) {
        this.mirrorSkin = mirrorSkin;
        isDirty = true;
        return this;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
