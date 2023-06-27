package de.oliver.fancynpcs.v1_20_1;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PacketReader_1_20_1 {

    /**
     * @param packets list of packets to listen for
     * @return true if successfully injected
     */
    public static boolean inject(ServerPlayer player, List<Class<? extends Packet>> packets) {
        Channel channel = player.connection.connection.channel;

        if (channel.pipeline().get("PacketInjector") != null) {
            return false;
        }

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out) {
                out.add(msg);
                if (!packets.contains(msg.getClass())) {
                    return;
                }


                PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(msg, player.getBukkitEntity());
                FancyLib.getScheduler().runTaskLater(null, 1L, packetReceivedEvent::callEvent);
            }
        });

        return true;
    }

}
