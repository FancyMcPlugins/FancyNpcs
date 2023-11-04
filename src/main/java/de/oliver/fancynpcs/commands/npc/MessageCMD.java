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

        String message = "";
        for (int i = 2; i < args.length; i++) {
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

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, message, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setMessage(message);
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
