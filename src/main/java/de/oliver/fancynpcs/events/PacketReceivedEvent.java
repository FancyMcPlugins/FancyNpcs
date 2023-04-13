package de.oliver.fancynpcs.events;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Currently only works for the ServerboundInteractPacket packet
 */
public class PacketReceivedEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final Packet<?> packet;
    private final Player player;

    public PacketReceivedEvent(Packet<?> packet, Player player) {
        this.packet = packet;
        this.player = player;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
