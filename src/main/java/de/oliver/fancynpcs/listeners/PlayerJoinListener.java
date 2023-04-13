package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.Npc;
import de.oliver.fancynpcs.PacketReader;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PacketReader packetReader = new PacketReader(event.getPlayer());
        boolean injected = packetReader.inject();

        if(!injected){
            MessageHelper.warning(event.getPlayer(), "Something went wrong. Interacting with NPCs will not work for you.");
            MessageHelper.warning(event.getPlayer(), "Rejoin might fix this bug");
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
            npc.spawn(event.getPlayer());
        }

        if(!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")){
            new Thread(() -> {
                ComparableVersion newestVersion = FancyNpcs.getInstance().getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if(newestVersion != null && newestVersion.compareTo(currentVersion) > 0){
                    MessageHelper.warning(event.getPlayer(), "You are using an outdated version of the FancyNpcs Plugin");
                    MessageHelper.warning(event.getPlayer(), "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyNpcs.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
                }
            }).start();
        }
    }
}
