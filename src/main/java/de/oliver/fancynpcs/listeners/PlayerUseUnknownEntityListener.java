package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerUseUnknownEntityListener implements Listener {

    private final List<UUID> interacts = new ArrayList<>();

    @EventHandler
    public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
        // On right click event is called twice, so we need to prevent that
        if (interacts.contains(event.getPlayer().getUniqueId())) return;
        interacts.add(event.getPlayer().getUniqueId());
        FancyNpcs.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(FancyNpcs.getInstance(),
                () -> interacts.remove(event.getPlayer().getUniqueId()), 5);

        if (event.getHand() != EquipmentSlot.HAND) return;

        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(event.getEntityId());
        if (npc == null) return;

        /* This is not needed
        if (npc.getData().getType() == EntityType.VILLAGER && event.getHand() == EquipmentSlot.HAND && event.getClickedRelativePosition() == null) {
            npc.interact(event.getPlayer());
            return;
        }

        if (!event.isAttack() && (event.getHand() == EquipmentSlot.HAND || event.getClickedRelativePosition() != null)) {
            return;
        }
        */

        npc.interact(event.getPlayer());
    }
}
