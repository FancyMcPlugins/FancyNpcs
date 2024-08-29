package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionInterruptException;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
        super("execute_random_action", true);
    }

    /**
     * Executes a random action triggered by the given action trigger on the specified NPC and player.
     *
     * @param trigger the action trigger that triggered the execution of this method
     * @param npc     the NPC on which the action will be executed
     * @param player  the player involved in the action execution
     * @param value   the value associated with the action
     */
    @Override
    public void execute(@NotNull ActionTrigger trigger, @NotNull Npc npc, Player player, String value) {
        List<NpcActionData> actions = npc.getData().getActions(trigger).stream()
                .filter(data -> !(data.action() instanceof ExecuteRandomActionAction)) // Prevent infinite recursion
                .toList();
        if (actions.isEmpty()) {
            return;
        }

        NpcActionData action = actions.get(new Random().nextInt(actions.size()));
        try {
            action.action().execute(trigger, npc, player, value);
        } catch (ActionInterruptException e) {
            throw new ActionInterruptException(npc, action.action());
        }
    }
}
