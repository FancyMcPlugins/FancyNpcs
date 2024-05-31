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
        ATTRIBUTE,
        COLLIDABLE,
        DISPLAY_NAME,
        EQUIPMENT,
        GLOWING,
        GLOWING_COLOR,
        INTERACTION_COOLDOWN,
        SCALE,
        LOCATION,
        MIRROR_SKIN,
        PLAYER_COMMAND,
        SERVER_COMMAND,
        SHOW_IN_TAB,
        SKIN,
        TURN_TO_PLAYER,
        TYPE,
        // Messages.
        MESSAGE_ADD,
        MESSAGE_SET,
        MESSAGE_REMOVE,
        MESSAGE_CLEAR,
        MESSAGE_SEND_RANDOMLY,
        // Player commands.
        PLAYER_COMMAND_ADD,
        PLAYER_COMMAND_SET,
        PLAYER_COMMAND_REMOVE,
        PLAYER_COMMAND_CLEAR,
        PLAYER_COMMAND_SEND_RANDOMLY,
        // Server commands.
        SERVER_COMMAND_ADD,
        SERVER_COMMAND_SET,
        SERVER_COMMAND_REMOVE,
        SERVER_COMMAND_CLEAR,
        SERVER_COMMAND_SEND_RANDOMLY
    }
}
