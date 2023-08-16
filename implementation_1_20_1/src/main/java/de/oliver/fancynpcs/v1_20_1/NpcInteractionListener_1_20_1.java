package de.oliver.fancynpcs.v1_20_1;

import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcInteractionListener;
import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class NpcInteractionListener_1_20_1 extends NpcInteractionListener {

    @Override
    @EventHandler
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof ServerboundInteractPacket interactPacket)) {
            return;
        }

        Player p = event.getPlayer();

        String hand = "";
        if (!interactPacket.isAttack()) {
            hand = ReflectionUtils.getValue(ReflectionUtils.getValue(interactPacket, "b"), "a").toString(); // ServerboundInteractPacket.InteractionAction.hand
        }

        int entityId = interactPacket.getEntityId();
        String action = ReflectionUtils.getValue(interactPacket, "b").toString(); // ServerboundInteractPacket.action
        boolean isSneaking = interactPacket.isUsingSecondaryAction();

        Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(entityId);
        if (npc == null) {
            return;
        }

        npc.interact(p, hand, action, isSneaking);
    }

    @Override
    public boolean injectPlayer(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        return PacketReader_1_20_1.inject(serverPlayer, List.of(ServerboundInteractPacket.class));
    }

}
