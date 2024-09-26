package de.oliver.fancynpcs.api.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a player command action that can be executed when triggered by an NPC interaction.
 */
public class PlayerCommandAction extends NpcAction {

    public PlayerCommandAction() {
        super("player_command", true);
    }

    /**
     * Executes a player command action when triggered by an NPC interaction.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        String command = ChatColorHandler.translate(value, context.getPlayer(), List.of(PlaceholderAPIParser.class));

        if (command.toLowerCase().startsWith("server")) {
            String[] args = value.split(" ");
            if (args.length < 2) {
                return;
            }
            String server = args[1];

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            context.getPlayer().sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
            return;
        }

        FancyNpcsPlugin.get().getScheduler().runTask(
                context.getPlayer().getLocation(),
                () -> {
                    try {
                        context.getPlayer().chat("/" + command);
                    } catch (Exception e) {
                        FancyNpcsPlugin.get().getFancyLogger().warn("Failed to execute command: " + command);
                    }
                });
    }
}
