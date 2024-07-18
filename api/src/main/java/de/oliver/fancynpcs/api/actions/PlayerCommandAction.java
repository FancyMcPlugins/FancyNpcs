package de.oliver.fancynpcs.api.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerCommandAction extends NpcAction {

    public PlayerCommandAction() {
        super("player_command");
    }

    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String command = ChatColorHandler.translate(value, player, List.of(PlaceholderAPIParser.class));

        if (command.toLowerCase().startsWith("server")) {
            String[] args = value.split(" ");
            if (args.length < 2) {
                return;
            }
            String server = args[1];

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
            return;
        }

        FancyNpcsPlugin.get().getScheduler().runTask(
                player.getLocation(),
                () -> player.chat("/" + command)
        );
    }
}
