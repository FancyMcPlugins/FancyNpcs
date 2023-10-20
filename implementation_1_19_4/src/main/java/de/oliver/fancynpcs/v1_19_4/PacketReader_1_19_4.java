package de.oliver.fancynpcs.v1_19_4;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

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
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof ServerboundInteractPacket interactPacket)) {
            return;
        }

        Player p = event.getPlayer();

        String handStr = "MAIN_HAND";
        if (interactPacket.getActionType() != ServerboundInteractPacket.ActionType.ATTACK) {
            handStr = ReflectionUtils.getValue(ReflectionUtils.getValue(interactPacket, "b"), "a").toString(); // ServerboundInteractPacket.InteractionAction.hand
        }

        int entityId = interactPacket.getEntityId();
        ServerboundInteractPacket.ActionType action = interactPacket.getActionType();
        boolean isSneaking = interactPacket.isUsingSecondaryAction();

        Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(entityId);
        if (npc == null) {
            return;
        }

        EquipmentSlot hand = handStr.equals("MAIN_HAND") ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;

        npc.interact(
                p,
                action == ServerboundInteractPacket.ActionType.ATTACK,
                hand,
                action == ServerboundInteractPacket.ActionType.INTERACT_AT ? new Vector(0, 0, 0) : null
        );
    }

}
