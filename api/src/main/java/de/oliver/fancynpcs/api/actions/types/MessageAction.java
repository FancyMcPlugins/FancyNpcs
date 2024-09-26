package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;
import org.jetbrains.annotations.NotNull;

/**
 * The MessageAction class represents an action that sends a message to the player when executed by an NPC.
 */
public class MessageAction extends NpcAction {

    public MessageAction() {
        super("message", true);
    }

    /**
     * Executes the action associated with this NpcAction.
     *
     * @param value The value passed to the action.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        context.getPlayer().sendMessage(ModernChatColorHandler.translate(value, context.getPlayer()));
    }
}
