package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when NPC starts looking at a player.
 */
public class NpcLookEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final Player player;
    private boolean isCancelled;

    public NpcLookEvent(@NotNull Npc npc, @NotNull Player player) {
        this.npc = npc;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the modified npc
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player who interacted with the npc
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

}
