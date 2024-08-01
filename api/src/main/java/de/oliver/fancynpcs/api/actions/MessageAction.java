package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageAction extends NpcAction {

    public MessageAction() {
        super("message", true);
    }

    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        player.sendMessage(ModernChatColorHandler.translate(value, player));
    }
}
