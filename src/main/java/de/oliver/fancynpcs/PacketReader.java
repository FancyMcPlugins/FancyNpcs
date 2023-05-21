package de.oliver.fancynpcs;

import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketReader {

    private final Player player;

    public PacketReader(Player player) {
        this.player = player;
    }

    public boolean inject() {
        Channel channel = FancyNpcs.getInstance().getNmsBase().getChannel(player);

        if (channel.pipeline().get("PacketInjector") != null) {
            return false;
        }

        //channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ServerboundInteractPacket msg, List<Object> out) {
                out.add(msg);

                PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(msg, player);
                FancyNpcs.getInstance().getScheduler().runTaskLater(null, 1L, packetReceivedEvent::callEvent);
            }
        });

        return true;
    }

}
