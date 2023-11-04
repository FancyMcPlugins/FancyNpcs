package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerCommandCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(player, lang.get("wrong-usage"));
            return false;
        }


        if (npc == null) {
            MessageHelper.error(player, lang.get("npc-not-found"));
            return false;
        }

        String cmd = "";
        for (int i = 2; i < args.length; i++) {
            cmd += args[i] + " ";
        }
        cmd = cmd.substring(0, cmd.length() - 1);

        if (cmd.equalsIgnoreCase("none")) {
            cmd = "";
        }

        for (String blockedCommand : FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands()) {
            if (cmd.toLowerCase().startsWith(blockedCommand.toLowerCase())) {
                MessageHelper.error(player, lang.get("illegal-command"));
                return false;
            }
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, cmd, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setPlayerCommand(cmd);
            MessageHelper.success(player, lang.get("npc-command-playerCommand-updated"));
        } else {
            MessageHelper.error(player, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
