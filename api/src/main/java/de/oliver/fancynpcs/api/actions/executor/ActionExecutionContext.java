package de.oliver.fancynpcs.api.actions.executor;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ActionExecutionContext {

    private final ActionTrigger trigger;
    private final Npc npc;
    private final List<NpcAction.NpcActionData> actions;
    private @Nullable Player player;
    private int actionIndex;

    public ActionExecutionContext(ActionTrigger trigger, Npc npc, @Nullable Player player) {
        this.trigger = trigger;
        this.npc = npc;
        this.player = player;

        this.actions = new ArrayList<>(npc.getData().getActions(trigger));
        this.actionIndex = 0;
    }

    public ActionExecutionContext(ActionTrigger trigger, Npc npc) {
        this(trigger, npc, null);
    }

    public void run(int index) {
        if (index < 0 || index >= actions.size()) {
            return;
        }

        NpcAction.NpcActionData actionData = actions.get(index);
        actionData.action().execute(this, actionData.value());
    }

    public void runNext() {
        if (actionIndex < 0 || actionIndex >= actions.size()) {
            return;
        }

        run(actionIndex++);
    }

    public boolean hasNext() {
        return actionIndex >= 0 && actionIndex < actions.size();
    }

    public void reset() {
        actionIndex = 0;
    }

    public void terminate() {
        actionIndex = -1;
    }

    public boolean isTerminated() {
        return actionIndex == -1;
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

    public @Nullable Player getPlayer() {
        return player;
    }

    public int getActionIndex() {
        return actionIndex;
    }
}
