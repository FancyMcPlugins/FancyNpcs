package de.oliver.fancynpcs.listeners;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.v1_19_4.PacketReader_1_19_4;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final LanguageConfig config = FancyNpcs.getInstance().getLanguageConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String mcVersion = Bukkit.getMinecraftVersion();
        if (mcVersion.equals("1.19.4")) {
            PacketReader_1_19_4.inject(event.getPlayer());
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
            npc.spawn(event.getPlayer());
        }

        if (!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            new Thread(() -> {
                ComparableVersion newestVersion = FancyNpcs.getInstance().getVersionFetcher().fetchNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if (newestVersion != null && newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(event.getPlayer(), config.get("join-update-outdated"));
                    MessageHelper.warning(event.getPlayer(), config.get(
                            "join-update-new_version",
                            "new_version", newestVersion.toString(),
                            "download_url", FancyNpcs.getInstance().getVersionFetcher().getDownloadUrl()
                    ));
                }
            }).start();
        }
    }
}
