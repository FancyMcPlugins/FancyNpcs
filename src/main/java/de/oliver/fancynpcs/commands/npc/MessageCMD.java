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
import java.util.Queue;

public class MessageCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        // /npc message <id> add <message> - appends a message to the list
        // /npc message <id> set <index> <message> - sets a message at the given index
        // /npc message <id> remove <index> - removes a message at the given index
        // /npc message <id> clear - clears all messages


        if (args.length == 3) {
            return List.of("add", "set", "remove", "clear");
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("remove")) {
                List<String> messages = new LinkedList<>();
                for (int i = 0; i < npc.getData().getMessages().size(); i++) {
                    messages.add(String.valueOf(i + 1));
                }
                return messages;
            }
        } else if (args.length == 5) {
            if (args[2].equalsIgnoreCase("set")) {
                int index;
                try {
                    index = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    return null;
                }

                if (index < 1 || index > npc.getData().getMessages().size()) {
                    return null;
                }

                return List.of(npc.getData().getMessages().get(index - 1));
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
            return clearMessages(receiver, npc, args);
        }

        if (args.length == 4 && args[2].equalsIgnoreCase("remove")) {
            return removeMessage(receiver, npc, args);
        }

        if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
            return addMessage(receiver, npc, args);
        }

        if (args.length >= 5 && args[2].equalsIgnoreCase("set")) {
            return setMessage(receiver, npc, args);
        }

        MessageHelper.error(receiver, lang.get("wrong-usage"));
        return false;
    }

    private boolean addMessage(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        String message = "";
        for (int i = 3; i < args.length; i++) {
            message += args[i] + " ";
        }

        message = message.substring(0, message.length() - 1);

        if (message.equalsIgnoreCase("none")) {
            message = "";
        }

        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && hasIllegalCommand(message.toLowerCase())) {
            MessageHelper.error(receiver, lang.get("illegal-command"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, message, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().addMessage(message);
            MessageHelper.success(receiver, lang.get("npc-command-message-updated"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean setMessage(CommandSender receiver, Npc npc, String[] args) {
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

        if (index < 1 || index > npc.getData().getMessages().size()) {
            MessageHelper.error(receiver, lang.get("npc-command-message-invalid-index"));
            return false;
        }

        String message = "";
        for (int i = 4; i < args.length; i++) {
            message += args[i] + " ";
        }

        message = message.substring(0, message.length() - 1);

        if (message.equalsIgnoreCase("none")) {
            message = "";
        }

        if (hasIllegalCommand(message.toLowerCase())) {
            MessageHelper.error(receiver, lang.get("illegal-command"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, message, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().getMessages().set(index - 1, message);
            MessageHelper.success(receiver, lang.get("npc-command-message-updated"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean removeMessage(CommandSender receiver, Npc npc, String[] args) {
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

        if (index < 1 || index > npc.getData().getMessages().size()) {
            MessageHelper.error(receiver, lang.get("npc-command-message-invalid-index"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, "", receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().removeMessage(index - 1);
            MessageHelper.success(receiver, lang.get("npc-command-message-updated"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    private boolean clearMessages(CommandSender receiver, Npc npc, String[] args) {
        if (args.length < 2) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, "", receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().getMessages().clear();
            MessageHelper.success(receiver, lang.get("npc-command-message-updated"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }

    // <click:run_command:"/op">TEST
    private boolean hasIllegalCommand(String message) {
        message = message.replace("/", "");

        char[] chars = message.toCharArray();
        Queue<String> tokens = new LinkedList<>();
        List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
        String currentWord = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ' ') {
                if (!currentWord.equals(" ") && !currentWord.equals(""))
                    tokens.add(currentWord);
                currentWord = "";
            } else if (c == '<' || c == '>' || c == ':') {
                if (!currentWord.equals(" ") && !currentWord.equals(""))
                    tokens.add(currentWord);
                tokens.add(String.valueOf(c));
                currentWord = "";
            } else {
                currentWord = currentWord + c;
            }
        }
        if (currentWord.length() > 0 && !currentWord.equals(" "))
            tokens.add(currentWord);

        while (!tokens.isEmpty()) {
            if (((String) tokens.poll()).equalsIgnoreCase("run_command") && ((String) tokens.poll()).equalsIgnoreCase(":")) {
                String command = tokens.poll();
                command = command.replace("\"", "");
                command = command.replace("'", "");
                command = command.replace("´", "");
                command = command.replace("`", "");
                for (String blockedCommand : blockedCommands) {
                    if (command.toLowerCase().startsWith(blockedCommand.toLowerCase())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
