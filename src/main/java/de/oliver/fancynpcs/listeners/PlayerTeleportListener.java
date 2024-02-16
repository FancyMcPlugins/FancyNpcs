package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerTeleportListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.checkAndUpdateVisibility(player);
        }
    }

}
