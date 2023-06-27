package de.oliver.fancynpcs.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PacketReceivedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Object packet;
    private final Player player;

    public PacketReceivedEvent(Object packet, Player player) {
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Object getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
