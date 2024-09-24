package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
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
     * @param context The context in which the action is being executed.
     * @param value   The value associated with the action. Can be null if no value is required.
     */
    public abstract void execute(@NotNull ActionExecutionContext context, @Nullable String value);

    public String getName() {
        return name;
    }

    public boolean requiresValue() {
        return requiresValue;
    }

    public record NpcActionData(int order, NpcAction action, String value) {
    }
}
