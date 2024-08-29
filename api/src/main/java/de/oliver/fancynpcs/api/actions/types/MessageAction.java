package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import org.bukkit.entity.Player;
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
     * @param trigger
     * @param npc     The Npc object on which the action is executed.
     * @param player  The Player object associated with the action.
     * @param value   The value passed to the action.
     */
    @Override
    public void execute(@NotNull ActionTrigger trigger, @NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (player == null) {
            return;
        }

        player.sendMessage(ModernChatColorHandler.translate(value, player));
    }
}
