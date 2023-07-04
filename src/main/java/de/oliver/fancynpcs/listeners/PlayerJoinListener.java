package de.oliver.fancynpcs.listeners;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcMessagesConfig;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final FancyNpcMessagesConfig config = FancyNpcs.getInstance().getMessagesConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean injected = FancyNpcs.getInstance().getNpcInteractionListener().injectPlayer(event.getPlayer());
        if (!injected) {
            MessageHelper.warning(event.getPlayer(), config.getString("join.inject.failed"));
            MessageHelper.warning(event.getPlayer(), config.getString("join.inject.rejoin"));
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
            npc.spawn(event.getPlayer());
        }

        if (!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            new Thread(() -> {
                ComparableVersion newestVersion = FancyNpcs.getInstance().getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if (newestVersion != null && newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(event.getPlayer(), config.getString("join.update.outdated"));
                    MessageHelper.warning(event.getPlayer(), config.getString("join.update.new_version")
                            .replace("$new_version", newestVersion.toString())
                            .replace("$download_url", FancyNpcs.getInstance().getVersionFetcher().getDownloadUrl())
                    );
                }
            }).start();
        }
    }
}
