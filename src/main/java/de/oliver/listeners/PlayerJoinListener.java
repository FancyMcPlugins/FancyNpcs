package de.oliver.listeners;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import de.oliver.PacketReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PacketReader packetReader = new PacketReader(event.getPlayer());
        packetReader.inject();

        for (Npc npc : NpcPlugin.getInstance().getNpcManager().getAllNpcs()) {
            npc.spawn(event.getPlayer());
        }
    }

}
