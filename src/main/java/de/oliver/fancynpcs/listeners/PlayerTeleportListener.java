package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcStartLookingEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerTeleportListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();
        int visibilityDistance = FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance();
        int turnToPlayerDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            NpcData npcData = npc.getData();
            Location npcLocation = npcData.getLocation();

            if (!npcData.isSpawnEntity()) {
                continue;
            }

            boolean isCurrentlyVisible = npc.getIsVisibleForPlayer().getOrDefault(player.getUniqueId(), false);
            if (playerLocation.getWorld() != npcLocation.getWorld()) {
                if (isCurrentlyVisible) {
                    npc.remove(player);
                }
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
