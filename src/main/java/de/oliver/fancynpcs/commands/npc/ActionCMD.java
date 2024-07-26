package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ActionCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc action <npc> <trigger> add <actionType> [value]")
    @Permission("fancynpcs.command.npc.action.add")
    public void onActionAdd(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull String actionType,
            final @Nullable @Greedy String value
    ) {
        //TODO: Implement this method
        sender.sendMessage("hello world");
    }

    @Command("npc action <npc> <trigger> set <number> <actionType> [value]")
    @Permission("fancynpcs.command.npc.action.set")
    public void onActionSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull @Argument(suggestions = "ActionCMD/number_range") Integer number,
            final @NotNull String actionType,
            final @Nullable @Greedy String value
    ) {
        //TODO: Implement this method
    }

    @Command("npc action <npc> <trigger> remove <number>")
    @Permission("fancynpcs.command.npc.action.remove")
    public void onMessageRemove(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @Argument(suggestions = "ActionCMD/number_range") int number
    ) {
        //TODO: Implement this method
    }

    @Command("npc action <npc> <trigger> clear")
    @Permission("fancynpcs.command.npc.action.clear")
    public void onMessageClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        //TODO: Implement this method
    }

    @Command("npc message <npc> <trigger> list")
    @Permission("fancynpcs.command.npc.action.list")
    public void onMessageList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        //TODO: Implement this method
    }

    @Command("npc message <npc> <trigger> send_randomly [state]")
    @Permission("fancynpcs.command.npc.action.send_randomly")
    public void onMessageSendRandomly(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @Nullable Boolean state
    ) {
        //TODO: Implement this method
    }


    /* PARSERS AND SUGGESTIONS */

    @Suggestions("ActionCMD/number_range")
    public List<String> suggestNumber(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.getOrDefault("npc", null);
        final ActionTrigger trigger = context.getOrDefault("trigger", null);

        if (npc == null || trigger == null) return Collections.emptyList();

        List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < npc.getData().getActions(trigger).size(); i++) {
            suggestions.add(String.valueOf(i + 1));
        }

        return suggestions;
    }
}