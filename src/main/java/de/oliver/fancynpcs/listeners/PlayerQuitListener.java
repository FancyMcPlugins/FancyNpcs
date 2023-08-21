package de.oliver.fancynpcs.listeners;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Changing isLookingAtPlayer state (of event player) to false.
        // This allows the NpcLookEvent to be called when player joins back. (Because otherwise, state would remain true and no change would be detected)
        for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
            npc.getIsLookingAtPlayer().put(event.getPlayer().getUniqueId(), false);
        }
    }
}
