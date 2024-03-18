package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import static de.oliver.fancynpcs.api.events.NpcInteractEvent.InteractionType;

public class PlayerUseUnknownEntityListener implements Listener {

    @EventHandler
    public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(event.getEntityId());
        // Skipping entities that are not FancyNpcs' NPCs
        if (npc == null)
            return;
        // PlayerUseUnknownEntityEvent can optionally be ALSO called for OFF-HAND slot. Making sure to run logic only ONCE.
        if (event.getHand() == EquipmentSlot.HAND)
            // PlayerUseUnknownEntityEvent can be called multiple times for interactions that are NOT attacks, making sure to run logic only ONCE.
            if (event.isAttack() || event.getClickedRelativePosition() == null)
                // Further interaction handling is done by Npc#interact method...
                npc.interact(event.getPlayer(), event.isAttack() ? InteractionType.LEFT_CLICK : InteractionType.RIGHT_CLICK);
    }

}
