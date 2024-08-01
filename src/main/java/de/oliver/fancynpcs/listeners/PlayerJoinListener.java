package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.v1_19_4.PacketReader_1_19_4;
import de.oliver.fancynpcs.v1_20.PacketReader_1_20;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String mcVersion = Bukkit.getMinecraftVersion();
        if (mcVersion.equals("1.19.4")) {
            PacketReader_1_19_4.inject(event.getPlayer());
        } else if (mcVersion.equals("1.20")) {
            PacketReader_1_20.inject(event.getPlayer());
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
            npc.getIsVisibleForPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsLookingAtPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
        }

        // don't spawn the npc for player if he just joined
        FancyNpcs.getInstance().getVisibilityTracker().addJoinDelayPlayer(event.getPlayer().getUniqueId());
        FancyNpcs.getInstance().getScheduler().runTaskLater(null, 20L * 2, () -> FancyNpcs.getInstance().getVisibilityTracker().removeJoinDelayPlayer(event.getPlayer().getUniqueId()));

        if (!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            FancyNpcs.getInstance().getScheduler().runTaskAsynchronously(
                    () -> FancyNpcs.getInstance().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true)
            );
        }
    }
}
