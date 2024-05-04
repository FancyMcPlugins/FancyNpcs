package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import me.dave.chatcolorhandler.ModernChatColorHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MessageCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc message <npc> add")
    @Permission("fancynpcs.command.npc.message.add")
    public void onMessageAdd(final CommandSender sender, final Npc npc) {
        translator.translate("npc_message_add_syntax").send(sender);
    }

    @Command("npc message <npc> add <message>")
    @Permission("fancynpcs.command.npc.message.add")
    public void onMessageAdd(final CommandSender sender, final Npc npc, final @Greedy String message) {
        // Exiting the command block in case banned command has been found in the message.
        if (hasBlockedCommands(message)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MESSAGE_SET, message, sender).callEvent()) {
            npc.getData().getMessages().add(message);
            // ...
            translator.translate("npc_message_add_success").replace("total", String.valueOf(npc.getData().getMessages().size())).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc message <npc> set <number> <message>")
    @Permission("fancynpcs.command.npc.message.set")
    public void onMessageSet(final CommandSender sender, final Npc npc, final int number, final @Greedy String message) {
        // Sending error message in case banned command has been found in the message.
        if (hasBlockedCommands(message)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Getting the total count of messages that are currently in the list.
        final int totalCount = npc.getData().getMessages().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_message_set_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message Exiting command block if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_message_set_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MESSAGE_SET, new Object[]{index, message}, sender).callEvent()) {
            npc.getData().getMessages().set(index, message);
            // ...
            translator.translate("npc_message_set_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc message <npc> remove")
    @Permission("fancynpcs.command.npc.message.remove")
    public void onMessageRemoveSyntax(final CommandSender sender, final Npc npc) {
        translator.translate("npc_message_remove_syntax").send(sender);
    }

    @Command("npc message <npc> remove <number>")
    @Permission("fancynpcs.command.npc.message.remove")
    public void onMessageRemove(final CommandSender sender, final Npc npc, final int number) {
        // Getting the total count of messages that are currently in the list.
        final int totalCount = npc.getData().getMessages().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_message_remove_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message Exiting command block if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_message_remove_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // Getting the message to pass to the NpcModifyEvent.
        final String message = npc.getData().getMessages().get(index);
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MESSAGE_REMOVE, new Object[]{index, message}, sender).callEvent()) {
            npc.getData().getMessages().remove(index);
            // ...
            translator.translate("npc_message_remove_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc message <npc> clear")
    @Permission("fancynpcs.command.npc.message.clear")
    public void onMessageClear(final CommandSender sender, final Npc npc) {
        final int total = npc.getData().getMessages().size();
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MESSAGE_CLEAR, null, sender).callEvent()) {
            npc.getData().getMessages().clear();
            translator.translate("npc_message_clear_success").replace("total", String.valueOf(total)).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc message <npc> list")
    @Permission("fancynpcs.command.npc.message.list")
    public void onMessageList(final CommandSender sender, final Npc npc) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Command("npc message <npc> send_randomly [state]")
    @Permission("fancynpcs.command.npc.message.send_randomly")
    public void onMessageSendRandomly(final CommandSender sender, final Npc npc, final @Nullable Boolean state) {
        final boolean finalState = state != null ? state : !npc.getData().isSendMessagesRandomly();
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MESSAGE_SEND_RANDOMLY, finalState, sender).callEvent()) {
            npc.getData().setSendMessagesRandomly(finalState);
            npc.updateForAll();
            translator.translate(finalState ? "npc_message_send_randomly_set_true" : "npc_message_send_randomly_set_false").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    // Returns 'true' if specified component contains blocked command, 'false' otherwise.
    private boolean hasBlockedCommands(final @NotNull String message) {
        // Converting message to a Component.
        final Component component = ModernChatColorHandler.translate(message);
        // Getting the list of all blocked commands.
        final List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
        // Iterating over all children of the component...
        return StreamSupport.stream(component.iterable(ComponentIteratorType.DEPTH_FIRST).spliterator(), false).anyMatch(it -> {
            final ClickEvent event = it.clickEvent();
            // We only care about click events with run_command as an action. Continuing if not found.
            if (event == null || event.action() != Action.RUN_COMMAND)
                return false;
            // Iterating over list of blocked commands...
            for (final String blockedCommand : blockedCommands) {
                // Transforming the command to a base command with trailed whitespaces and slashes. This also removes namespaced part from the beginning of the command.
                final String transformedBaseCommand = blockedCommand.replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "");
                // Comparing click event value with the transformed base command. Returning the result.
                if (event.value().replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "").equalsIgnoreCase(transformedBaseCommand))
                    return true;
            }
            // Returning false as no blocked commands has been found.
            return false;
        });
    }

}
