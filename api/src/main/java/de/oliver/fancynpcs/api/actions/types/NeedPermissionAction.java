package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;

public class NeedPermissionAction extends NpcAction {

    public NeedPermissionAction() {
        super("need_permission", true);
    }

    @Override
    public void execute(ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        if (!context.getPlayer().hasPermission(value)) {
            FancyNpcsPlugin.get().getTranslator().translate("action_missing_permissions").send(context.getPlayer());
            context.terminate();
        }
    }
}
