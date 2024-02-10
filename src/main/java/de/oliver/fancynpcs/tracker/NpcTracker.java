package de.oliver.fancynpcs.tracker;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcStartLookingEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class NpcTracker extends BukkitRunnable {

    // Caching to avoid unnecessary look-ups.
    private static final NpcAttribute INVISIBLE_ATTRIBUTE = FancyNpcs.getInstance().getAttributeManager().getAttributeByName(EntityType.PLAYER, "invisible");

    @Override
    public void run() {
        Collection<Npc> npcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs();
        int visibilityDistance = FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance();
        int turnToPlayerDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();

            for (Npc npc : npcs) {
                NpcData npcData = npc.getData();
                Location npcLocation = npcData.getLocation();

                if (!npcData.isSpawnEntity()) {
                    continue;
                }

                boolean isCurrentlyVisible = npc.getIsVisibleForPlayer().getOrDefault(player.getUniqueId(), false);

                // Do not send information about invisible NPCs. Glowing NPCs are still shown.
                if (npcData.getAttributes().getOrDefault(INVISIBLE_ATTRIBUTE, "").equalsIgnoreCase("true") && !npcData.isGlowing()) {
                    // Removing NPC if currently visible by player.
                    if (isCurrentlyVisible) {
                        npc.remove(player);
                    }
                    // Skipping execution of the rest of the logic.
                    continue;
                }

                // Hide NPCs from other worlds/dimensions.
                if (playerLocation.getWorld() != npcLocation.getWorld() && isCurrentlyVisible) {
                    npc.remove(player);
                    continue;
                }

                double distance = playerLocation.distance(npcLocation);
                if (Double.isNaN(distance)) {
                    continue;
                }

                if (distance > visibilityDistance && isCurrentlyVisible) {
                    npc.remove(player);
                } else if (distance < visibilityDistance && !isCurrentlyVisible) {
                    npc.spawn(player);
                }

                if (npcData.isTurnToPlayer() && distance < turnToPlayerDistance) {
                    Location newLoc = playerLocation.clone();
                    newLoc.setDirection(newLoc.subtract(npcLocation).toVector());
                    npc.lookAt(player, newLoc);
                    // Setting NPC to be looking at the player and getting the value previously stored (or not) inside a map.
                    Boolean wasPreviouslyLooking = npc.getIsLookingAtPlayer().put(player.getUniqueId(), true);
                    // Comparing the previous state with current state to prevent event from being called continuously.
                    if (wasPreviouslyLooking == null || !wasPreviouslyLooking) {
                        // Calling NpcStartLookingEvent from the main thread.
                        FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                            Bukkit.getPluginManager().callEvent(new NpcStartLookingEvent(npc, player));
                        });
                    }
                    // Updating state if changed.
                } else if (npcData.isTurnToPlayer() && npc.getIsLookingAtPlayer().getOrDefault(player.getUniqueId(), false)) {
                    npc.getIsLookingAtPlayer().put(player.getUniqueId(), false);
                    // Calling NpcStopLookingEvent from the main thread.
                    FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                        Bukkit.getPluginManager().callEvent(new NpcStopLookingEvent(npc, player));
                    });
                }
            }
        }
    }
}
