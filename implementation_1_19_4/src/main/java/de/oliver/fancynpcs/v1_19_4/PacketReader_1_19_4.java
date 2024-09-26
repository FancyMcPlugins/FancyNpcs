package de.oliver.fancynpcs.v1_19_4;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class PacketReader_1_19_4 implements Listener {

    public static boolean inject(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = serverPlayer.connection.connection.channel;

        if (channel.pipeline().get("PacketInjector") != null) {
            return false;
        }

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ServerboundInteractPacket msg, List<Object> out) {
                out.add(msg);

                PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(msg, player);
                FancyLib.getScheduler().runTaskLater(null, 1L, packetReceivedEvent::callEvent);
            }
        });

        return true;
    }

    @EventHandler
    public void onPacketReceived(final PacketReceivedEvent event) {
        // Skipping packets other than ServerboundInteractPacket...
        if (!(event.getPacket() instanceof ServerboundInteractPacket interactPacket))
            return;
        // Getting NPC from entity identifier.
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(interactPacket.getEntityId());
        // Skipping entities that are not FancyNpcs' NPCs...
        if (npc == null)
            return;
        // Getting interaction information.
        final boolean isAttack = (interactPacket.getActionType() == ServerboundInteractPacket.ActionType.ATTACK);
        final boolean isInteract = (interactPacket.getActionType() == ServerboundInteractPacket.ActionType.INTERACT_AT);
        final EquipmentSlot hand = (interactPacket.getActionType() == ServerboundInteractPacket.ActionType.ATTACK)
                ? EquipmentSlot.HAND
                : ReflectionUtils.getValue(ReflectionUtils.getValue(interactPacket, "b"), "a").toString().equals("MAIN_HAND") // ServerboundInteractPacket.InteractionAction.hand
                ? EquipmentSlot.HAND
                : EquipmentSlot.OFF_HAND;
        // This can optionally be ALSO called for OFF-HAND slot. Making sure to run logic only ONCE.
        if (hand == EquipmentSlot.HAND) {
            // This packet can be sent multiple times for interactions that are NOT attacks, making sure to run logic only ONCE.
            if (isAttack || !isInteract || npc.getData().getType() == EntityType.ARMOR_STAND) {
                npc.interact(event.getPlayer(), ActionTrigger.ANY_CLICK);
                npc.interact(event.getPlayer(), isAttack ? ActionTrigger.LEFT_CLICK : ActionTrigger.RIGHT_CLICK);
            }
        }
    }

}
