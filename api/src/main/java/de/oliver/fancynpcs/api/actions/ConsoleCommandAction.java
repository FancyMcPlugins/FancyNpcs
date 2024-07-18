package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConsoleCommandAction extends NpcAction {

    public ConsoleCommandAction() {
        super("console_command");
    }

    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String command = value.replace("{player}", player.getName());
        String finalCommand = ChatColorHandler.translate(command, player, List.of(PlaceholderAPIParser.class));

        FancyNpcsPlugin.get().getScheduler().runTask(null, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
    }
}
