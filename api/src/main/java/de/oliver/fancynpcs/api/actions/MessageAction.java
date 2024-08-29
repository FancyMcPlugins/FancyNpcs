package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;
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
     * @param npc    The Npc object on which the action is executed.
     * @param player The Player object associated with the action.
     * @param value  The value passed to the action.
     */
    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        player.sendMessage(ModernChatColorHandler.translate(value, player));
    }
}
