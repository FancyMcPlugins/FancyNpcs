package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when NPC stops looking at a player.
 */
public class NpcStopLookingEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final Player player;

    public NpcStopLookingEvent(@NotNull Npc npc, @NotNull Player player) {
        this.npc = npc;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the npc that stopped looking at a player
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player who npc stopped looking at
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
