package de.oliver.listeners;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import de.oliver.PacketReader;
import de.oliver.utils.VersionFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PacketReader packetReader = new PacketReader(event.getPlayer());
        packetReader.inject();

        Npc.isTeamCreated.put(event.getPlayer().getUniqueId(), false);

        for (Npc npc : NpcPlugin.getInstance().getNpcManager().getAllNpcs()) {
            npc.spawn(event.getPlayer());
        }

        if(event.getPlayer().hasPermission("NpcPlugin.admin")){
            String newestVersion = VersionFetcher.getNewestVersion();
            if(!newestVersion.equals(NpcPlugin.getInstance().getDescription().getVersion())){
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] You are using an outdated version of the NPC Plugin.</color>"));
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>.</color>"));
            }
        }
    }

}
