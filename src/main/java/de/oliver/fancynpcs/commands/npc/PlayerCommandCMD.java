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

import java.util.LinkedList;
import java.util.List;

public class PlayerCommandCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player playedr, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length == 3) {
            return List.of("add", "set", "remove", "clear");
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("remove")) {
                List<String> commands = new LinkedList<>();
                for (int i = 0; i < npc.getData().getPlayerCommands().size(); i++) {
                    commands.add(String.valueOf(i + 1));
                }
                return commands;
            }
        } else if (args.length == 5) {
            if (args[2].equalsIgnoreCase("set")) {
                int index;
                try {
                    index = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    return null;
                }

                if (index < 1 || index > npc.getData().getPlayerCommands().size()) {
                    return null;
                }

                return List.of(npc.getData().getPlayerCommands().get(index - 1));
            }
        }

        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (args.length == 3 && args[2].equalsIgnoreCase("clear")) {
            return clearCommand(receiver, npc, args);
        }

        if (args.length == 4 && args[2].equalsIgnoreCase("remove")) {
            return removeCommand(receiver, npc, args);
        }

        if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
            return addCommand(receiver, npc, args);
        }

        if (args.length >= 5 && args[2].equalsIgnoreCase("set")) {
            return setCommand(receiver, npc, args);
        }

        MessageHelper.error(receiver, lang.get("wrong-usage"));
        return false;
    }

    private boolean addCommand(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        String command = "";
        for (int i = 3; i < args.length; i++) {
            command += args[i] + " ";
        }

        command = command.substring(0, command.length() - 1);

        if (command.equalsIgnoreCase("none")) {
            command = "";
        }

        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && isBlockedCommand(command.toLowerCase())) {
            MessageHelper.error(receiver, lang.get("illegal-command"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, command, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().addPlayerCommand(command);
            MessageHelper.success(receiver, lang.get("npc-command-playercommand-updated", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean setCommand(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 4) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        int index;
        try {
            index = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        if (index < 1 || index > npc.getData().getPlayerCommands().size()) {
            MessageHelper.error(receiver, lang.get("npc-command-playercommand-invalid-index", "input", String.valueOf(index)));
            return false;
        }

        String command = "";
        for (int i = 4; i < args.length; i++) {
            command += args[i] + " ";
        }

        command = command.substring(0, command.length() - 1);

        if (command.equalsIgnoreCase("none")) {
            command = "";
        }

        if (isBlockedCommand(command.toLowerCase())) {
            MessageHelper.error(receiver, lang.get("illegal-command"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, command, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().getPlayerCommands().set(index - 1, command);
            MessageHelper.success(receiver, lang.get("npc-command-playercommand-updated", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean removeCommand(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        int index;
        try {
            index = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        if (index < 1 || index > npc.getData().getPlayerCommands().size()) {
            MessageHelper.error(receiver, lang.get("npc-command-playercommand-invalid-index", "input", String.valueOf(index)));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, "", receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().removePlayerCommand(index - 1);
            MessageHelper.success(receiver, lang.get("npc-command-playercommand-updated", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean clearCommand(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 2) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, "", receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().getPlayerCommands().clear();
            MessageHelper.success(receiver, lang.get("npc-command-playercommand-updated", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean isBlockedCommand(String cmd) {
        for (String blockedCommand : FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands()) {
            if (cmd.equalsIgnoreCase(blockedCommand) || cmd.toLowerCase().startsWith(blockedCommand.toLowerCase() + " ")) {
                return true;
            }
        }

        return false;
    }
}
