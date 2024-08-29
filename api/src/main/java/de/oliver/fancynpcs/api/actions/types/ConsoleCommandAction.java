package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.NpcAction;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a console command action that can be executed for an NPC.
 */
public class ConsoleCommandAction extends NpcAction {

    public ConsoleCommandAction() {
        super("console_command", true);
    }

    /**
     * Executes the console command action for an NPC.
     *
     * @param npc    The NPC object.
     * @param player The player executing the command.
     * @param value  The command string to be executed. The value can contain the placeholder "{player}" which will be replaced with the player's name.
     */
    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String command = value;
        if (player != null) {
            command = value.replace("{player}", player.getName());
        }

        String finalCommand = ChatColorHandler.translate(command, player, List.of(PlaceholderAPIParser.class));

        FancyNpcsPlugin.get().getScheduler().runTask(null, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
    }
}
