package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The NpcAction class is an abstract class that represents an action that can be performed by an NPC.
 * Each NpcAction has a name and a flag indicating whether it requires a value.
 * <p>
 * The NpcAction class provides an abstract execute method that must be implemented by subclasses
 * to specify the behavior of the action when executed.
 * <p>
 * Subclasses of NpcAction can provide additional data using the NpcActionData record, which includes
 * an order value to specify the order of execution, the NpcAction itself, and a value associated with
 * the action.
 * <p>
 * This class provides getters for the name and the requiresValue flag of the action.
 */
public abstract class NpcAction {

    private final String name;
    private final boolean requiresValue;

    public NpcAction(String name, boolean requiresValue) {
        this.name = name;
        this.requiresValue = requiresValue;
    }

    /**
     * Executes the action associated with this NpcAction.
     *
     * @param trigger The trigger that caused the action to be executed.
     * @param npc     The Npc on which the action will be executed.
     * @param player  The Player involved in the action. Can be null if no player is involved.
     * @param value   The value associated with the action. Can be null if no value is required.
     */
    public abstract void execute(@NotNull ActionTrigger trigger, @NotNull Npc npc, @Nullable Player player, @Nullable String value);

    public String getName() {
        return name;
    }

    public boolean requiresValue() {
        return requiresValue;
    }

    public record NpcActionData(int order, NpcAction action, String value) {
    }
}
