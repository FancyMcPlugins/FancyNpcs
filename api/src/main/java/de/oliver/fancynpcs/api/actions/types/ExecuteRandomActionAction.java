package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * The ExecuteRandomActionAction class represents an action that can be executed randomly by an NPC.
 * <p>
 * The ExecuteRandomActionAction class provides an implementation for the execute method,
 * which executes a random action triggered by the given action trigger on the specified NPC and player.
 * The execution of the action is based on the actions associated with the NPC's data for the given trigger.
 */
public class ExecuteRandomActionAction extends NpcAction {

    public ExecuteRandomActionAction() {
        super("execute_random_action", false);
    }

    /**
     * Executes a random action triggered by the given action trigger on the specified NPC and player.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        int currentIndex = context.getActionIndex();
        int actionCount = context.getActions().size();

        int randomIndex = getRandomIndex(currentIndex, actionCount);

        NpcActionData action = context.getActions().get(randomIndex);
        action.action().execute(context, action.value());

        context.terminate();
    }

    private int getRandomIndex(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }
}
