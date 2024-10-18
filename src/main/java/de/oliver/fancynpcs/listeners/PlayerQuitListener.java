package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
            // Changing isLookingAtPlayer state (of event player) to false.
            // This allows the NpcStartLookingEvent to be called when player joins back. (Because otherwise, state would remain true and no change would be detected)
            npc.getIsVisibleForPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsLookingAtPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
            // Calling NpcStopLookingEvent.
            Bukkit.getPluginManager().callEvent(new NpcStopLookingEvent(npc, event.getPlayer()));
        }
    }
}
