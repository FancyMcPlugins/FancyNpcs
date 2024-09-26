package de.oliver.fancynpcs.api.actions.executor;

public class ActionExecutor {

    public static void execute(ActionExecutionContext context) {
        new Thread(() -> {
            while (context.hasNext()) {
                context.runNext();
            }
        }, "NpcActionExecutor").start();
    }

}
