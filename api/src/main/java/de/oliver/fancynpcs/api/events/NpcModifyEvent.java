package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a NPC is being modified
 */
public class NpcModifyEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final NpcModification modification;
    @NotNull
    private final Object newValue;
    @NotNull
    private final CommandSender modifier;
    private boolean isCancelled;

    public NpcModifyEvent(@NotNull Npc npc, @NotNull NpcModification modification, Object newValue, @NotNull CommandSender modifier) {
        this.npc = npc;
        this.modification = modification;
        this.newValue = newValue;
        this.modifier = modifier;
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
     * @return the modification that was being made
     */
    public @NotNull NpcModification getModification() {
        return modification;
    }

    /**
     * @return the value that is being set
     */
    public @NotNull Object getNewValue() {
        return newValue;
    }

    /**
     * @return the sender who modified the npc
     */
    public @NotNull CommandSender getModifier() {
        return modifier;
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

    public enum NpcModification {
        LOCATION,
        SKIN,
        DISPLAY_NAME,
        EQUIPMENT,
        SERVER_COMMAND,
        PLAYER_COMMAND,
        SHOW_IN_TAB,
        GLOWING,
        GLOWING_COLOR,
        TURN_TO_PLAYER,
        CUSTOM_MESSAGE,
        TYPE,
        ATTRIBUTE,
        COLLIDABLE,
    }
}
