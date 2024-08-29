package de.oliver.fancynpcs.api.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import org.bukkit.entity.Player;
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
     * @param trigger
     * @param npc     The Npc object on which the action is being executed.
     * @param player  The player involved in the action.
     * @param value   The value associated with the action.
     */
    @Override
    public void execute(@NotNull ActionTrigger trigger, @NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (player == null) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(value);
        player.sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
    }
}
