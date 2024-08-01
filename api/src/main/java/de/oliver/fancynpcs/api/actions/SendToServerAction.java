package de.oliver.fancynpcs.api.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SendToServerAction extends NpcAction {

    public SendToServerAction() {
        super("send_to_server", true);
    }

    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(value);
        player.sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
    }
}
