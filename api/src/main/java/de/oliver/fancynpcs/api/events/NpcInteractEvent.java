package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.utils.InteractionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Is fired when a player interacts with a NPC
 */
public class NpcInteractEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @Nullable
    private final List<NpcAction.NpcActionData> actions;
    @NotNull
    private final Consumer<Player> onClick;
    @NotNull
    private final Player player;
    private final InteractionType interactionType;
    private boolean isCancelled;

    public NpcInteractEvent(@NotNull Npc npc, @NotNull Consumer<Player> onClick, @NotNull List<NpcAction.NpcActionData> actions, @NotNull Player player, @NotNull InteractionType interactionType) {
        this.npc = npc;
        this.onClick = onClick;
        this.actions = actions;
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
     * @return the custom on click method that will run
     */
    public @NotNull Consumer<Player> getOnClick() {
        return onClick;
    }

    /**
     * @return the actions that will run
     */
    public @Nullable List<NpcAction.NpcActionData> getActions() {
        return actions;
    }

    /**
     * @return returns interaction type
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

}
