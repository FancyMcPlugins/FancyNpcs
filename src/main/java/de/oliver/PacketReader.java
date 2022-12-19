package de.oliver;

import de.oliver.events.PacketReceivedEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketReader {

    private final Player player;

    public PacketReader(Player player) {
        this.player = player;
    }

    public boolean inject(){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Channel channel = craftPlayer.getHandle().connection.connection.channel;

        if(channel.pipeline().get("PacketInjector") != null){
            return false;
        }

        //channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ServerboundInteractPacket msg, List<Object> out) {
                out.add(msg);

                PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(msg, player);
                Bukkit.getScheduler().runTaskLater(NpcPlugin.getInstance(), packetReceivedEvent::callEvent, 1L);
            }
        });

        NpcPlugin.getInstance().getLogger().info("Injected player " + player.getName());

        return true;
    }

}
