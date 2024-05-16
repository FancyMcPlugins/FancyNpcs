package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public enum ServerCommandCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc server_command <npc> add <command>")
    @Permission("fancynpcs.command.npc.server_command.add")
    public void onServerCommandAdd(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "ServerCommandCMD/commands") @Greedy String command
    ) {
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(command)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Calling the event and adding server command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND_ADD, command, sender).callEvent()) {
            npc.getData().getServerCommands().add(command);
            translator.translate("npc_server_command_add_success").replace("total", String.valueOf(npc.getData().getServerCommands().size())).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc server_command <npc> set <number> <command>")
    @Permission("fancynpcs.command.npc_server_command.set")
    public void onServerCommandSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(suggestions = "ServerCommandCMD/number_range") int number,
            final @NotNull @Argument(suggestions = "ServerCommandCMD/commands") @Greedy String command
    ) {
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(command)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Getting the total count of server commands that are currently in the list.
        final int totalCount = npc.getData().getServerCommands().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_server_command_set_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_server_command_set_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // Calling the event and setting server command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND_SET, new Object[]{index, command}, sender).callEvent()) {
            npc.getData().getServerCommands().set(index, command);
            translator.translate("npc_server_command_set_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc server_command <npc> remove <number>")
    @Permission("fancynpcs.command.npc_server_command.remove")
    public void onServerCommandRemove(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(suggestions = "ServerCommandCMD/number_range") int number
    ) {
        // Getting the total count of server commands that are currently in the list.
        final int totalCount = npc.getData().getServerCommands().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_server_command_remove_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_server_command_remove_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // Getting the message to pass to the NpcModifyEvent.
        final String command = npc.getData().getServerCommands().get(index);
        // Calling the event and removing server command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND_REMOVE, new Object[]{index, command}, sender).callEvent()) {
            npc.getData().getServerCommands().remove(index);
            // Sending success message to the sender.
            translator.translate("npc_server_command_remove_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc server_command <npc> clear")
    @Permission("fancynpcs.command.npc_server_command.clear")
    public void onServerCommandClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        final int total = npc.getData().getServerCommands().size();
        // Calling the event and clearing server commands if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND_CLEAR, null, sender).callEvent()) {
            npc.getData().getServerCommands().clear();
            translator.translate("npc_server_command_clear_success").replace("total", String.valueOf(total)).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc server_command <npc> list")
    @Permission("fancynpcs.command.npc_server_command.list")
    public void onServerCommandList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Sending error message if the list is empty.
        if (npc.getData().getServerCommands().isEmpty()) {
            translator.translate("npc_server_command_list_failure_empty").send(sender);
            return;
        }
        translator.translate("npc_server_command_list_header").send(sender);
        // Iterating over all server commands attached to this NPC and sending them to the sender.
        for (int i = 0; i < npc.getData().getServerCommands().size(); i++) {
            final String command = npc.getData().getServerCommands().get(i);
            translator.translate("npc_server_command_list_entry")
                    .replace("number", String.valueOf(i + 1))
                    .replace("command", command)
                    .send(sender);
        }
        final int totalCount = npc.getData().getServerCommands().size();
        translator.translate("npc_server_command_list_footer")
                .replace("total", String.valueOf(totalCount))
                .replace("total_formatted", "· ".repeat(3 - String.valueOf(totalCount).length()) + totalCount)
                .send(sender);
    }

    /* ARGUMENT PARSERS AND SUGGESTION PROVIDERS */

    @Suggestions("ServerCommandCMD/number_range")
    // Generates number range suggestions based on the number of server commands.
    public List<String> suggestNumber(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.getOrDefault("npc", null);
        return npc == null || npc.getData().getServerCommands().isEmpty()
                ? Collections.emptyList()
                : new ArrayList<>() {{
            for (int i = 0; i < npc.getData().getServerCommands().size(); i++)
                add(String.valueOf(i + 1));
        }};
    }

    @Suggestions("ServerCommandCMD/commands") // Suggests allowed (non-blocked) commands accessible by the command sender.
    public Collection<String> suggestCommand(final CommandContext<CommandSender> context, final CommandInput input) {
        return Bukkit.getServer().getCommandMap().getKnownCommands().values().stream()
                .filter(command -> !command.getName().contains(":") && command.testPermission(context.sender()) && !hasBlockedCommands(command.getName()))
                .map(org.bukkit.command.Command::getName)
                .toList();
    }

    /* UTILITY METHODS */

    /**
     * Returns {@code true} if specified string contains a blocked command, {@code false} otherwise.
     */
    private boolean hasBlockedCommands(final @NotNull String string) {
        // Getting the list of all blocked commands.
        final List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
        // Iterating over list of blocked commands...
        for (final String blockedCommand : blockedCommands) {
            // Transforming the command to a base command with trailed whitespaces and slashes. This also removes namespaced part from the beginning of the command.
            final String transformedBaseCommand = blockedCommand.replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "");
            // Comparing click event value with the transformed base command. Returning the result.
            if (string.replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "").equalsIgnoreCase(transformedBaseCommand))
                return true;
        }
        // Returning false as no blocked commands has been found.
        return false;
    }

}
