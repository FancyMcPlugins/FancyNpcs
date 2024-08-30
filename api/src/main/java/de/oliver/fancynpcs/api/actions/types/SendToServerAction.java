package de.oliver.fancynpcs.api.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;

/**
 * The SendToServerAction class is a subclass of NpcAction that represents an action
 * to send data to the server using BungeeCord messaging.
 */
public class SendToServerAction extends NpcAction {

    public SendToServerAction() {
        super("send_to_server", true);
    }

    /**
     * Executes the action associated with this NpcAction.
     *
     * @param value The value associated with the action.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(value);
        context.getPlayer().sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
    }
}
