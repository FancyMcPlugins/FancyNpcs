package de.oliver.fancynpcs.api.actions.executor;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.types.BlockUntilDoneAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Context for executing a sequence of NPC actions initiated by different triggers.
 */
public class ActionExecutionContext {

    /**
     * The trigger that initiated the action.
     * This is a final variable that represents the specific condition or
     * event that caused the action to be created in the context.
     */
    private final ActionTrigger trigger;

    /**
     * The NPC that the action is being executed on.
     */
    private final Npc npc;

    /**
     * The player involved in the action, may be null if no player is involved.
     */
    private final @Nullable UUID player;

    /**
     * A list of NpcActionData instances representing the sequence of actions
     * to be executed for the NPC in the given context.
     */
    private final List<NpcAction.NpcActionData> actions;

    /**
     * The index of the currently executing action in the list of actions.
     * <p>
     * This variable keeps track of which action within the action sequence
     * is currently being executed. It is incremented as actions are executed
     * sequentially using the {@link #runNext()} method.
     * </p>
     * <p>
     * The default initial value is 0, indicating the start of the sequence.
     * When the index is set to -1, it signifies that the sequence has been
     * terminated and no further actions should be executed.
     * </p>
     */
    private int actionIndex;

    /**
     * Constructs an ActionExecutionContext with the specified ActionTrigger, Npc, and an optional Player.
     *
     * @param trigger the trigger that initiated the action
     * @param npc     the NPC that the action is being executed on
     * @param player  the player involved in the action, may be null if no player is involved
     */
    public ActionExecutionContext(ActionTrigger trigger, Npc npc, @Nullable UUID player) {
        this.trigger = trigger;
        this.npc = npc;
        this.player = player;

        this.actions = new ArrayList<>(npc.getData().getActions(trigger));
        this.actionIndex = 0;
    }

    /**
     * Constructs an ActionExecutionContext with the specified ActionTrigger and Npc, without a Player.
     *
     * @param trigger the trigger that initiated the action
     * @param npc     the NPC that the action is being executed on
     */
    public ActionExecutionContext(ActionTrigger trigger, Npc npc) {
        this(trigger, npc, null);
    }

    /**
     * Executes the action at the specified index within the list of actions.
     *
     * @param index the index of the action to be executed. If the index is out of bounds, the method returns immediately.
     */
    public void run(int index) {
        if (index < 0 || index >= actions.size()) {
            return;
        }

        NpcAction.NpcActionData actionData = actions.get(index);
        actionData.action().execute(this, actionData.value());
    }

    /**
     * Executes the next action in the list of actions.
     * <p>
     * If the current action index is out of bounds, the method returns immediately.
     * The action index is incremented after the action is executed.
     * </p>
     */
    public void runNext() {
        if (actionIndex < 0 || actionIndex >= actions.size()) {
            return;
        }

        run(actionIndex++);
    }

    /**
     * Checks if there are more actions to be executed.
     *
     * @return true if there are more actions to be executed, false otherwise
     */
    public boolean hasNext() {
        return actionIndex >= 0 && actionIndex < actions.size();
    }

    /**
     * Resets the current action index to its initial state.
     * This is useful for re-running the sequence of actions from the beginning.
     */
    public void reset() {
        actionIndex = 0;
    }

    /**
     * Terminates the current action sequence by setting the action index to -1.
     * This effectively marks the context as finished and prevents any further actions from being executed.
     */
    public void terminate() {
        actionIndex = -1;
    }

    /**
     * Checks if the action sequence has been terminated.
     *
     * @return true if the action index is -1, indicating the sequence is terminated; false otherwise
     */
    public boolean isTerminated() {
        return actionIndex == -1;
    }

    public boolean shouldBlockUntilDone() {
        for (NpcAction.NpcActionData action : actions) {
            if (action.action() instanceof BlockUntilDoneAction) {
                return true;
            }
        }

        return false;
    }

    public ActionTrigger getTrigger() {
        return trigger;
    }

    public Npc getNpc() {
        return npc;
    }

    public List<NpcAction.NpcActionData> getActions() {
        return actions;
    }

    public UUID getPlayerUUID() {
        return player;
    }

    public @Nullable Player getPlayer() {
        if (player == null) {
            return null;
        }

        return Bukkit.getPlayer(player);
    }

    public int getActionIndex() {
        return actionIndex;
    }
}
