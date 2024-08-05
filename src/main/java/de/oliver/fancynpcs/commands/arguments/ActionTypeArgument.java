package de.oliver.fancynpcs.commands.arguments;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.Nullable;

public class ActionTypeArgument {

    public static final ActionTypeArgument INSTANCE = new ActionTypeArgument();
    private final static FancyNpcs PLUGIN = FancyNpcs.getInstance();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private ActionTypeArgument() {
    }

    @Parser(name = "", suggestions = "action_type")
    public NpcAction parse(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString();
        final @Nullable NpcAction action = PLUGIN.getActionManager().getActionByName(value);
        if (action == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_action_type").replaceStripped("input", value).send(context.sender()));
        return action;
    }

    @Suggestions("action_type")
    public Iterable<String> suggestions(final CommandContext<CommandSender> context, final CommandInput input) {
        return PLUGIN.getActionManager()
                .getAllActions()
                .stream()
                .map(NpcAction::getName)
                .toList();
    }

}
