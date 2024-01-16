package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerUseUnknownEntityListener implements Listener {

    @EventHandler
    public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(event.getEntityId());
        if (npc == null) {
            return;
        }

        if (npc.getData().getType() == EntityType.VILLAGER && event.getHand() == EquipmentSlot.HAND && event.getClickedRelativePosition() == null) {
            npc.interact(event.getPlayer());
            return;
        }

        if (!event.isAttack() && (event.getHand() == EquipmentSlot.HAND || event.getClickedRelativePosition() != null)) {
            return;
        }

        npc.interact(event.getPlayer());
    }

}
