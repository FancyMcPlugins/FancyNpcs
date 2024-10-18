package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.lushplugins.chatcolorhandler.parsers.ParserTypes;

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
     * @param value The command string to be executed. The value can contain the placeholder "{player}" which will be replaced with the player's name.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String command = value;
        if (context.getPlayer() != null) {
            command = value.replace("{player}", context.getPlayer().getName());
        }

        String finalCommand = ChatColorHandler.translate(command, context.getPlayer(), ParserTypes.placeholder());

        FancyNpcsPlugin.get().getScheduler().runTask(null, () -> {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            } catch (Exception e) {
                FancyNpcsPlugin.get().getFancyLogger().warn("Failed to execute command: " + finalCommand);
            }
        });
    }
}
