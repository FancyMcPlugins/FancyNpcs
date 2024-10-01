package de.oliver.fancynpcs.api;

import de.oliver.fancylib.RandomUtils;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutor;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.Interval.Unit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Npc {

    private static final NpcAttribute INVISIBLE_ATTRIBUTE = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "invisible");
    private static final char[] localNameChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};
    protected final Map<UUID, Boolean> isTeamCreated = new ConcurrentHashMap<>();
    protected final Map<UUID, Boolean> isVisibleForPlayer = new ConcurrentHashMap<>();
    protected final Map<UUID, Boolean> isLookingAtPlayer = new ConcurrentHashMap<>();
    protected final Map<UUID, Long> lastPlayerInteraction = new ConcurrentHashMap<>();
    private final Translator translator = FancyNpcsPlugin.get().getTranslator();
    protected NpcData data;
    protected boolean saveToFile;

    public Npc(NpcData data) {
        this.data = data;
        this.saveToFile = true;
    }

    protected String generateLocalName() {
        String localName = "";
        for (int i = 0; i < 8; i++) {
            localName += "&" + localNameChars[(int) RandomUtils.randomInRange(0, localNameChars.length)];
        }

        localName = ChatColor.translateAlternateColorCodes('&', localName);

        return localName;
    }

    public abstract void create();

    public abstract void spawn(Player player);

    public void spawnForAll() {
        FancyNpcsPlugin.get().getNpcThread().submit(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                spawn(onlinePlayer);
            }
        });
    }

    public abstract void remove(Player player);

    public void removeForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            remove(onlinePlayer);
        }
    }

    /**
     * Checks if the NPC should be visible for the player.
     *
     * @param player The player to check for.
     * @return True if the NPC should be visible for the player, otherwise false.
     */
    protected boolean shouldBeVisible(Player player) {
        int visibilityDistance = FancyNpcsPlugin.get().getFancyNpcConfig().getVisibilityDistance();

        if (!data.isSpawnEntity()) {
            return false;
        }

        if (data.getLocation() == null) {
            return false;
        }

        if (player.getLocation().getWorld() != data.getLocation().getWorld()) {
            return false;
        }

        double distanceSquared = data.getLocation().distanceSquared(player.getLocation());
        if (distanceSquared > visibilityDistance * visibilityDistance) {
            return false;
        }

        if (FancyNpcsPlugin.get().getFancyNpcConfig().isSkipInvisibleNpcs() && data.getAttributes().getOrDefault(INVISIBLE_ATTRIBUTE, "false").equalsIgnoreCase("true") && !data.isGlowing() && data.getEquipment().isEmpty()) {
            return false;
        }

        return true;
    }

    public void checkAndUpdateVisibility(Player player) {
        FancyNpcsPlugin.get().getNpcThread().submit(() -> {
            boolean shouldBeVisible = shouldBeVisible(player);
            boolean wasVisible = isVisibleForPlayer.getOrDefault(player.getUniqueId(), false);

            if (shouldBeVisible && !wasVisible) {
                spawn(player);
            } else if (!shouldBeVisible && wasVisible) {
                remove(player);
            }
        });
    }

    public abstract void lookAt(Player player, Location location);

    public abstract void update(Player player);

    public void updateForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            update(onlinePlayer);
        }
    }

    public abstract void move(Player player, boolean swingArm);

    public void move(Player player) {
        move(player, true);
    }

    public void moveForAll(boolean swingArm) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            move(onlinePlayer, swingArm);
        }
    }

    public void moveForAll() {
        moveForAll(true);
    }

    public void interact(Player player) {
        interact(player, ActionTrigger.CUSTOM);
    }

    public void interact(Player player, ActionTrigger actionTrigger) {
        if (data.getInteractionCooldown() > 0) {
            final long interactionCooldownMillis = (long) (data.getInteractionCooldown() * 1000);
            final long lastInteractionMillis = lastPlayerInteraction.getOrDefault(player.getUniqueId(), 0L);
            final Interval interactionCooldownLeft = Interval.between(lastInteractionMillis + interactionCooldownMillis, System.currentTimeMillis(), Unit.MILLISECONDS);
            if (interactionCooldownLeft.as(Unit.MILLISECONDS) > 0) {

                if (!FancyNpcsPlugin.get().getFancyNpcConfig().isInteractionCooldownMessageDisabled()) {
                    translator.translate("interaction_on_cooldown").replace("time", interactionCooldownLeft.toString()).send(player);
                }

                return;
            }
            lastPlayerInteraction.put(player.getUniqueId(), System.currentTimeMillis());
        }

        List<NpcAction.NpcActionData> actions = data.getActions(actionTrigger);
        NpcInteractEvent npcInteractEvent = new NpcInteractEvent(this, data.getOnClick(), actions, player, actionTrigger);
        npcInteractEvent.callEvent();

        if (npcInteractEvent.isCancelled()) {
            return;
        }

        // onClick
        if (data.getOnClick() != null) {
            data.getOnClick().accept(player);
        }

        // actions
        ActionExecutor.execute(actionTrigger, this, player);
    }

    protected abstract void refreshEntityData(Player serverPlayer);

    public abstract int getEntityId();

    public NpcData getData() {
        return data;
    }

    public abstract float getEyeHeight();

    public Map<UUID, Boolean> getIsTeamCreated() {
        return isTeamCreated;
    }

    public Map<UUID, Boolean> getIsVisibleForPlayer() {
        return isVisibleForPlayer;
    }

    public Map<UUID, Boolean> getIsLookingAtPlayer() {
        return isLookingAtPlayer;
    }

    public Map<UUID, Long> getLastPlayerInteraction() {
        return lastPlayerInteraction;
    }

    public boolean isDirty() {
        return data.isDirty();
    }

    public void setDirty(boolean dirty) {
        data.setDirty(dirty);
    }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
    }
}
