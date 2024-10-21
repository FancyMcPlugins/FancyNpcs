package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerUseUnknownEntityListener implements Listener {

    @EventHandler
    public void onPlayerUseUnknownEntity(final PlayerUseUnknownEntityEvent event) {
        final Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(event.getEntityId());
        // Skipping entities that are not FancyNpcs' NPCs
        if (npc == null)
            return;
        // PlayerUseUnknownEntityEvent can optionally be ALSO called for OFF-HAND slot. Making sure to run logic only ONCE.
        if (event.getHand() == EquipmentSlot.HAND) {
            // PlayerUseUnknownEntityEvent can be called multiple times for interactions that are NOT attacks, making sure to run logic only ONCE.
            if (event.isAttack() || event.getClickedRelativePosition() == null || npc.getData().getType() == EntityType.ARMOR_STAND) {
                npc.interact(event.getPlayer(), event.isAttack() ? ActionTrigger.LEFT_CLICK : ActionTrigger.RIGHT_CLICK);
            }
        }
    }

}
