package de.oliver.fancynpcs.tracker;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class NpcTracker extends BukkitRunnable {

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
                }
            }
        }
    }
}
