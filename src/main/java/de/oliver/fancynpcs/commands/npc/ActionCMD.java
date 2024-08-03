package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
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

public class ActionCMD {
    public static final ActionCMD INSTANCE = new ActionCMD();
    private final static FancyNpcs PLUGIN = FancyNpcs.getInstance();

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private ActionCMD() {
    }

    @Command("npc action <npc> <trigger> add <actionType> [value]")
    @Permission("fancynpcs.command.npc.action.add")
    public void onActionAdd(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull NpcAction actionType,
            final @Nullable @Greedy String value
    ) {
        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator
                    .translate("npc_action_requires_value")
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions().getOrDefault(trigger, new ArrayList<>());

        npc.getData().addAction(trigger, currentActions.size() + 1, actionType, value);
        translator
                .translate("npc_action_add_success")
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> set <number> <actionType> [value]")
    @Permission("fancynpcs.command.npc.action.set")
    public void onActionSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull @Argument(suggestions = "ActionCMD/number_range") Integer number,
            final @NotNull NpcAction actionType,
            final @Nullable @Greedy String value
    ) {
        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator
                    .translate("npc_action_requires_value")
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        currentActions.set(number - 1, new NpcAction.NpcActionData(number, actionType, value));
        npc.getData().setActions(trigger, currentActions);
        translator
                .translate("npc_action_set_success")
                .replaceStripped("number", String.valueOf(number))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> remove <number>")
    @Permission("fancynpcs.command.npc.action.remove")
    public void onActionRemove(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @Argument(suggestions = "ActionCMD/number_range") int number
    ) {
        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);

        currentActions.remove(number - 1);
        npc.getData().setActions(trigger, currentActions);
        translator
                .translate("npc_action_remove_success")
                .replaceStripped("number", String.valueOf(number))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> clear")
    @Permission("fancynpcs.command.npc.action.clear")
    public void onActionClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        npc.getData().setActions(trigger, new ArrayList<>());
        translator
                .translate("npc_action_clear_success")
                .send(sender);
    }

    @Command("npc action <npc> <trigger> list")
    @Permission("fancynpcs.command.npc.action.list")
    public void onActionList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        List<NpcAction.NpcActionData> actions = npc.getData().getActions(trigger);
        if (actions.isEmpty()) {
            translator
                    .translate("npc_action_list_failure_empty")
                    .send(sender);
            return;
        }

        translator
                .translate("npc_action_list_header")
                .replaceStripped("trigger", trigger.name())
                .send(sender);

        for (int i = 0; i < actions.size(); i++) {
            NpcAction.NpcActionData action = actions.get(i);
            translator
                    .translate("npc_action_list_entry")
                    .replaceStripped("number", String.valueOf(action.order()))
                    .replaceStripped("action", action.action().getName())
                    .replaceStripped("value", action.value())
                    .send(sender);
        }

        translator
                .translate("npc_action_list_footer")
                .replaceStripped("total", String.valueOf(actions.size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> send_randomly [state]")
    @Permission("fancynpcs.command.npc.action.send_randomly")
    public void onActionSendRandomly(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @Nullable Boolean state
    ) {
        //TODO: Implement this method
        //test
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