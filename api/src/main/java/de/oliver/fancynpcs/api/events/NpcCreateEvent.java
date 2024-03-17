package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a new NPC is being created
 */
public class NpcCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final CommandSender creator;
    private boolean isCancelled;

    public NpcCreateEvent(@NotNull Npc npc, @NotNull CommandSender creator) {
        this.npc = npc;
        this.creator = creator;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the created npc
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player who created the npc
     */
    public @NotNull CommandSender getCreator() {
        return creator;
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
