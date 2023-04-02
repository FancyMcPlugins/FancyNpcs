package de.oliver.events;

import de.oliver.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Is fired when a player interacts with a NPC
 */
public class NpcInteractEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    @NotNull
    private final Npc npc;
    @Nullable
    private final String playerCommand;
    @Nullable
    private final String serverCommand;
    @NotNull
    private final Consumer<Player> onClick;
    @NotNull
    private final Player player;

    public NpcInteractEvent(@NotNull Npc npc, @Nullable String playerCommand, @Nullable String serverCommand, @NotNull Consumer<Player> onClick, @NotNull Player player) {
        this.npc = npc;
        this.playerCommand = playerCommand;
        this.serverCommand = serverCommand;
        this.onClick = onClick;
        this.player = player;
    }

    /**
     * @return the modified npc
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the command that the player will be forced to run
     */
    public @Nullable String getPlayerCommand() {
        return playerCommand;
    }

    /**
     * @return the command that the server will run
     */
    public @Nullable String getServerCommand() {
        return serverCommand;
    }

    /**
     * @return the custom on click method that will run
     */
    public @NotNull Consumer<Player> getOnClick() {
        return onClick;
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

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
