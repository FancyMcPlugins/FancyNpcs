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

public enum PlayerCommandCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc player_command <npc> add <command>")
    @Permission("fancynpcs.command.npc.player_command.add")
    public void onPlayerCommandAdd(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "PlayerCommandCMD/commands") @Greedy String command
    ) {
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(command)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Calling the event and adding player command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND_ADD, command, sender).callEvent()) {
            npc.getData().getPlayerCommands().add(command);
            translator.translate("npc_player_command_add_success").replace("total", String.valueOf(npc.getData().getPlayerCommands().size())).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc player_command <npc> set <number> <command>")
    @Permission("fancynpcs.command.npc.player_command.set")
    public void onPlayerCommandSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(suggestions = "PlayerCommandCMD/number_range") int number,
            final @NotNull @Argument(suggestions = "PlayerCommandCMD/commands") @Greedy String command
    ) {
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(command)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Getting the total count of player commands that are currently in the list.
        final int totalCount = npc.getData().getPlayerCommands().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_player_command_set_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_player_command_set_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // Calling the event and setting player command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND_SET, new Object[]{index, command}, sender).callEvent()) {
            npc.getData().getPlayerCommands().set(index, command);
            translator.translate("npc_player_command_set_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc player_command <npc> remove <number>")
    @Permission("fancynpcs.command.npc.player_command.remove")
    public void onPlayerCommandRemove(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(suggestions = "PlayerCommandCMD/number_range") int number
    ) {
        // Getting the total count of player commands that are currently in the list.
        final int totalCount = npc.getData().getPlayerCommands().size();
        // Sending error message if the list is empty.
        if (totalCount == 0) {
            translator.translate("npc_player_command_remove_failure_list_is_empty").send(sender);
            return;
        }
        // Sending error message if provided number is lower than 0 or higher than the list size.
        if (number < 1 || number > totalCount) {
            translator.translate("npc_player_command_remove_failure_not_in_range").replace("input", String.valueOf(number)).replace("max", String.valueOf(totalCount)).send(sender);
            return;
        }
        // User-specified number starts from 1, while index starts from 0. Subtracting 1 from the provided number to get the list index.
        final int index = number - 1;
        // Getting the message to pass to the NpcModifyEvent.
        final String command = npc.getData().getPlayerCommands().get(index);
        // Calling the event and removing player command if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND_REMOVE, new Object[]{index, command}, sender).callEvent()) {
            npc.getData().getPlayerCommands().remove(index);
            // Sending success message to the sender.
            translator.translate("npc_player_command_remove_success")
                    .replace("number", String.valueOf(number))
                    .replace("total", String.valueOf(totalCount)) // Total count remains the same, no entry has been added/removed from the list.
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc player_command <npc> clear")
    @Permission("fancynpcs.command.npc.player_command.clear")
    public void onPlayerCommandClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        final int total = npc.getData().getPlayerCommands().size();
        // Calling the event and clearing player commands if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND_CLEAR, null, sender).callEvent()) {
            npc.getData().getPlayerCommands().clear();
            translator.translate("npc_player_command_clear_success").replace("total", String.valueOf(total)).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc player_command <npc> list")
    @Permission("fancynpcs.command.npc.player_command.list")
    public void onPlayerCommandList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Sending error message if the list is empty.
        if (npc.getData().getPlayerCommands().isEmpty()) {
            translator.translate("npc_player_command_list_failure_empty").send(sender);
            return;
        }
        translator.translate("npc_player_command_list_header").send(sender);
        // Iterating over all player commands attached to this NPC and sending them to the sender.
        for (int i = 0; i < npc.getData().getPlayerCommands().size(); i++) {
            final String command = npc.getData().getPlayerCommands().get(i);
            translator.translate("npc_player_command_list_entry")
                    .replace("number", String.valueOf(i + 1))
                    .replace("command", command)
                    .send(sender);
        }
        final int totalCount = npc.getData().getPlayerCommands().size();
        translator.translate("npc_player_command_list_footer")
                .replace("total", String.valueOf(totalCount))
                .replace("total_formatted", "· ".repeat(3 - String.valueOf(totalCount).length()) + totalCount)
                .send(sender);
    }

    /* PARSERS AND SUGGESTIONS */

    @Suggestions("PlayerCommandCMD/number_range") // Generates number range suggestions based on the number of player commands.
    public List<String> suggestNumber(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.getOrDefault("npc", null);
        return npc == null || npc.getData().getPlayerCommands().isEmpty()
                ? Collections.emptyList()
                : new ArrayList<>() {{
                    for (int i = 0; i < npc.getData().getPlayerCommands().size(); i++)
                        add(String.valueOf(i + 1));
                }};
    }

    @Suggestions("PlayerCommandCMD/commands") // Suggests allowed (non-blocked) commands accessible by the command sender.
    public Collection<String> suggestCommand(final CommandContext<CommandSender> context, final CommandInput input) {
        return Bukkit.getServer().getCommandMap().getKnownCommands().values().stream()
                .filter(command -> !command.getName().contains(":") && command.testPermissionSilent(context.sender()) && !hasBlockedCommands(command.getName()))
                .map(org.bukkit.command.Command::getName)
                .toList();
    }

    /* UTILITY METHODS */

    /** Returns {@code true} if specified string contains a blocked command, {@code false} otherwise. */
    private boolean hasBlockedCommands(final @NotNull String string) {
        // Getting the list of all blocked commands.
        final List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
        // Iterating over all elements of the component.
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
