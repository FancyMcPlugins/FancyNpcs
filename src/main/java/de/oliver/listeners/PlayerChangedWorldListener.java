package de.oliver.listeners;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
        for (Npc npc : NpcPlugin.getInstance().getNpcManager().getAllNpcs()) {
            npc.spawn(event.getPlayer());
        }
    }

}
