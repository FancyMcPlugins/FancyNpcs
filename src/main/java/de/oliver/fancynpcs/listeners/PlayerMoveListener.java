package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location loc = event.getTo();
        Player p = event.getPlayer();

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            if (!npc.getData().isSpawnEntity()) {
                continue;
            }

            if (loc.getWorld() != npc.getData().getLocation().getWorld()) {
                continue;
            }

            double distance = loc.distance(npc.getData().getLocation());
            if (Double.isNaN(distance))
                continue;

            boolean isCurrentlyVisible = npc.getIsVisibleForPlayer().getOrDefault(p.getUniqueId(), false);
            if (distance > FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance() && isCurrentlyVisible) {
                npc.remove(p);
            } else if (distance < FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance() && !isCurrentlyVisible) {
                npc.spawn(p);
            }

            if (npc.getData().isTurnToPlayer() && distance < FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance()) {
                Location newLoc = loc.clone();
                newLoc.setDirection(newLoc.subtract(npc.getData().getLocation()).toVector());
                npc.lookAt(p, newLoc);
            }
        }
    }
}
