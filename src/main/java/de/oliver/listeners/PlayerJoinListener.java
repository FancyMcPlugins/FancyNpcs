package de.oliver.listeners;

import de.oliver.Npc;
import de.oliver.FancyNpcs;
import de.oliver.PacketReader;
import de.oliver.utils.VersionFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PacketReader packetReader = new PacketReader(event.getPlayer());
        packetReader.inject();


        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
            npc.spawn(event.getPlayer());
        }

        if(!FancyNpcs.getInstance().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")){
            new Thread(() -> {
                ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if(newestVersion != null && newestVersion.compareTo(currentVersion) > 0){
                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] You are using an outdated version of the FancyNpcs Plugin.</color>"));
                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>.</color>"));
                }
            }).start();
        }
    }

}
