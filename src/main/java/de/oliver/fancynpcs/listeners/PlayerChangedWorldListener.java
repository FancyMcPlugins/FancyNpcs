package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.Npc;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.spawn(event.getPlayer());
        }
    }

}
