package de.oliver.fancynpcs.api.actions.executor;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionExecutor {

    private static final Map<String, ActionExecutionContext> runningContexts = new ConcurrentHashMap<>();

    public static void execute(ActionTrigger trigger, Npc npc, Player player) {
        String key = getKey(trigger, npc, player);
        ActionExecutionContext runningContext = runningContexts.get(key);
        if (runningContext != null) {
            if (runningContext.shouldBlockUntilDone() && !runningContext.isTerminated()) {
                return;
            }
        }

        ActionExecutionContext context = new ActionExecutionContext(trigger, npc, player.getUniqueId());
        runningContexts.put(key, context);

        FancyNpcsPlugin.get().newThread("FancyNpcs-ActionExecutor", () -> {
            while (context.hasNext()) {
                context.runNext();
            }
            context.terminate();

            runningContexts.remove(key);
        }).start();

    }

    private static String getKey(ActionTrigger trigger, Npc npc, Player player) {
        return trigger.name() + "_" + npc.getData().getId() + "_" + player.getUniqueId();
    }

}
