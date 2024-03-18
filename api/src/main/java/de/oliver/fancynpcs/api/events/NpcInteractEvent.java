package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Is fired when a player interacts with a NPC
 */
public class NpcInteractEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @Nullable
    private final List<String> playerCommands;
    @Nullable
    private final String serverCommand;
    @NotNull
    private final Consumer<Player> onClick;
    @NotNull
    private final Player player;
    private final InteractionType interactionType;
    private boolean isCancelled;

    public NpcInteractEvent(@NotNull Npc npc, @Nullable List<String> playerCommands, @Nullable String serverCommand, @NotNull Consumer<Player> onClick, @NotNull Player player, @Nullable InteractionType interactionType) {
        this.npc = npc;
        this.playerCommands = playerCommands;
        this.serverCommand = serverCommand;
        this.onClick = onClick;
        this.player = player;
        this.interactionType = interactionType;
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
     * @return the commands that the player will be forced to run
     */
    public @Nullable List<String> getPlayerCommands() {
        return playerCommands;
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
     * @return returns click type
     */
    public @NotNull InteractionType getInteractionType() {
        return interactionType;
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

    public enum InteractionType {
        LEFT_CLICK,
        RIGHT_CLICK,
        /** {@link InteractionType#CUSTOM InteractionType#CUSTOM} represents interactions invoked by the API. */
        CUSTOM
    }

}
