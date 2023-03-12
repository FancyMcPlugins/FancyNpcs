package de.oliver.listeners;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Location loc = event.getTo();

        for (Npc npc : NpcPlugin.getInstance().getNpcManager().getAllNpcs()) {
            if(!npc.isTurnToPlayer()){
                continue;
            }

            if(loc.getWorld() != npc.getLocation().getWorld()){
                continue;
            }

            double distance = loc.distance(npc.getLocation());

            if(Double.isNaN(distance) || distance > 5){
                continue;
            }

            CraftPlayer cp = ((CraftPlayer) event.getPlayer());
            ServerPlayer sp = cp.getHandle();

            Location newLoc = loc.clone();
            newLoc.setDirection(newLoc.subtract(npc.getLocation()).toVector());
            npc.lookAt(sp, newLoc);
        }
    }

}
