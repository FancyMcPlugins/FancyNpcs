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
    private boolean glowing;
    private NamedTextColor glowingColor;
    private EntityType type;
    private Map<NpcEquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private boolean turnToPlayer;
    private String serverCommand;
    private String playerCommand;
    private String message;
    private Map<NpcAttribute, String> attributes;
    private boolean onlyVisibleToEnabled;
    private final List<String> onlyVisibleTo;
    private boolean isDirty;

    public NpcData(
            String id,
            String name,
            UUID creator,
            String displayName,
            SkinFetcher skin,
            Location location,
            boolean showInTab,
            boolean spawnEntity,
            boolean glowing,
            NamedTextColor glowingColor,
            EntityType type,
            Map<NpcEquipmentSlot, ItemStack> equipment,
            boolean turnToPlayer,
            Consumer<Player> onClick,
            String message,
            String serverCommand,
            String playerCommand,
            Map<NpcAttribute, String> attributes,
            boolean onlyVisibleToEnabled,
            List<String> onlyVisibleTo
    ) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.displayName = displayName;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.spawnEntity = spawnEntity;
        this.glowing = glowing;
        this.glowingColor = glowingColor;
        this.type = type;
        this.equipment = equipment;
        this.onClick = onClick;
        this.turnToPlayer = turnToPlayer;
        this.serverCommand = serverCommand;
        this.playerCommand = playerCommand;
        this.message = message;
        this.attributes = attributes;
        this.onlyVisibleToEnabled = onlyVisibleToEnabled;
        this.onlyVisibleTo = onlyVisibleTo;
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
        this.glowing = false;
        this.glowingColor = NamedTextColor.WHITE;
        this.onClick = p -> {
        };
        this.turnToPlayer = false;
        this.message = "";
        this.equipment = new HashMap<>();
        this.attributes = new HashMap<>();
        this.onlyVisibleToEnabled = false;
        this.onlyVisibleTo = new ArrayList<>();
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

    public String getServerCommand() {
        return serverCommand;
    }

    public NpcData setServerCommand(String serverCommand) {
        this.serverCommand = serverCommand;
        isDirty = true;
        return this;
    }

    public String getPlayerCommand() {
        return playerCommand;
    }

    public NpcData setPlayerCommand(String playerCommand) {
        this.playerCommand = playerCommand;
        isDirty = true;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public NpcData setMessage(String message) {
        this.message = message;
        isDirty = true;
        return this;
    }

    public Map<NpcAttribute, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(NpcAttribute attribute, String value) {
        attributes.put(attribute, value);
    }

    public void applyAllAttributes(Npc npc) {
        for (NpcAttribute attribute : attributes.keySet()) {
            attribute.apply(npc, attributes.get(attribute));
        }
    }

    public boolean isOnlyVisibleToEnabled() {
        return onlyVisibleToEnabled;
    }

    public NpcData setOnlyVisibleTo(boolean isEnabled) {
        this.onlyVisibleToEnabled = isEnabled;
        if (!isEnabled) onlyVisibleTo.clear();
        isDirty = true;
        return this;
    }

    public List<String> getOnlyVisibleToPlayers() {
        return onlyVisibleTo;
    }

    public void showToPlayer(UUID uuid) {
        if (!onlyVisibleToEnabled) setOnlyVisibleTo(true);
        if (!onlyVisibleTo.contains(uuid.toString())) {
            onlyVisibleTo.add(uuid.toString());
            isDirty = true;
        }
    }

    public void hideFromPlayer(UUID uuid) {
        if (onlyVisibleTo.remove(uuid.toString())) isDirty = true;
        if (onlyVisibleTo.isEmpty()) setOnlyVisibleTo(false);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
