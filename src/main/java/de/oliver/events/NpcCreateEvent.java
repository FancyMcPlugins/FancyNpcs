package de.oliver.events;

import de.oliver.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a new NPC is being created
 */
public class NpcCreateEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    @NotNull
    private final Npc npc;
    @NotNull
    private final Player player;

    public NpcCreateEvent(@NotNull Npc npc, @NotNull Player player) {
        this.npc = npc;
        this.player = player;
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
    public @NotNull Player getPlayer() {
        return player;
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

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
