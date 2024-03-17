package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a NPC is being deleted
 */
public class NpcRemoveEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final CommandSender receiver;
    private boolean isCancelled;

    public NpcRemoveEvent(@NotNull Npc npc, @NotNull CommandSender receiver) {
        this.npc = npc;
        this.receiver = receiver;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the npc that is being removed
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player who removed the npc
     */
    public @NotNull CommandSender getSender() {
        return receiver;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
