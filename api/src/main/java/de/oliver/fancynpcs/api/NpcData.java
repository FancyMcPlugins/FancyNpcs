package de.oliver.fancynpcs.api;

import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class NpcData {

    private final String id;
    private final String name;
    private final UUID creator;
    private String displayName;
    private SkinData skin;
    private boolean mirrorSkin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private boolean collidable;
    private boolean glowing;
    private NamedTextColor glowingColor;
    private EntityType type;
    private Map<NpcEquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private Map<ActionTrigger, List<NpcAction.NpcActionData>> actions;
    private boolean turnToPlayer;
    private float interactionCooldown;
    private float scale;
    private int visibilityDistance;
    private Map<NpcAttribute, String> attributes;
    private boolean isDirty;

    public NpcData(
            String id,
            String name,
            UUID creator,
            String displayName,
            SkinData skin,
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
            Map<ActionTrigger, List<NpcAction.NpcActionData>> actions,
            float interactionCooldown,
            float scale,
            int visibilityDistance,
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
        this.actions = actions;
        this.turnToPlayer = turnToPlayer;
        this.interactionCooldown = interactionCooldown;
        this.scale = scale;
        this.visibilityDistance = visibilityDistance;
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
        this.actions = new ConcurrentHashMap<>();
        this.turnToPlayer = false;
        this.interactionCooldown = 0;
        this.scale = 1;
        this.visibilityDistance = -1;
        this.equipment = new ConcurrentHashMap<>();
        this.attributes = new ConcurrentHashMap<>();
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

    public SkinData getSkinData() {
        return skin;
    }

    /**
     * Sets the skin data of the npc
     * Use this method, if you have a loaded skin data object (with texture and signature), otherwise use {@link #setSkin(String, SkinData.SkinVariant)}
     *
     * @param skinData the skin data
     */
    public NpcData setSkinData(SkinData skinData) {
        this.skin = skinData;
        isDirty = true;
        return this;
    }

    /**
     * Loads the skin data and sets it as the skin of the npc
     *
     * @param skin    a valid UUID, username, URL or file path
     * @param variant the skin variant
     */
    public NpcData setSkin(String skin, SkinData.SkinVariant variant) {
        SkinData data = FancyNpcsPlugin.get().getSkinManager().getByIdentifier(skin, variant);
        return setSkinData(data);
    }

    /**
     * Loads the skin data and sets it as the skin of the npc
     *
     * @param skin a valid UUID, username, URL or file path
     */
    public NpcData setSkin(String skin) {
        return setSkin(skin, SkinData.SkinVariant.AUTO);
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

    public Map<ActionTrigger, List<NpcAction.NpcActionData>> getActions() {
        return actions;
    }

    public NpcData setActions(Map<ActionTrigger, List<NpcAction.NpcActionData>> actions) {
        this.actions = actions;
        isDirty = true;
        return this;
    }

    public List<NpcAction.NpcActionData> getActions(ActionTrigger trigger) {
        return actions.getOrDefault(trigger, new ArrayList<>());
    }

    public NpcData setActions(ActionTrigger trigger, List<NpcAction.NpcActionData> actions) {
        this.actions.put(trigger, actions);
        isDirty = true;
        return this;
    }

    public NpcData addAction(ActionTrigger trigger, int order, NpcAction action, String value) {
        List<NpcAction.NpcActionData> a = actions.getOrDefault(trigger, new ArrayList<>());
        a.add(new NpcAction.NpcActionData(order, action, value));
        actions.put(trigger, a);

        isDirty = true;
        return this;
    }

    public NpcData removeAction(ActionTrigger trigger, NpcAction action) {
        List<NpcAction.NpcActionData> a = actions.getOrDefault(trigger, new ArrayList<>());
        a.removeIf(ad -> ad.action().equals(action));
        actions.put(trigger, a);

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

    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    public NpcData setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
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
