package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.NpcImpl;
import net.minecraft.server.level.ServerPlayer;
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

        for (NpcImpl npc : FancyNpcs.getInstance().getNpcManager().getAllNpcsImpl()) {
            if (!npc.isSpawnEntity()) {
                continue;
            }

            if (loc.getWorld() != npc.getLocation().getWorld()) {
                continue;
            }

            ServerPlayer sp = FancyNpcs.getInstance().getNmsBase().getServerPlayer(p);

            double distance = loc.distance(npc.getLocation());
            if (Double.isNaN(distance))
                continue;

            boolean isCurrentlyVisible = npc.getIsVisibleForPlayer().getOrDefault(p.getUniqueId(), false);

            if (distance > FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance() && isCurrentlyVisible) {
                npc.remove(p);
            } else if (distance < FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance() && !isCurrentlyVisible) {
                npc.spawn(p);
            }

            if (npc.isTurnToPlayer() && distance < FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance()) {
                Location newLoc = loc.clone();
                newLoc.setDirection(newLoc.subtract(npc.getLocation()).toVector());
                npc.lookAt(sp, newLoc);
            }
        }
    }
}
