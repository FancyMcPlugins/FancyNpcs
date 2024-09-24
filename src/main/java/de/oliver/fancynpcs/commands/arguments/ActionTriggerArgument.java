package de.oliver.fancynpcs.commands.arguments;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActionTriggerArgument {

    public static final ActionTriggerArgument INSTANCE = new ActionTriggerArgument();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private ActionTriggerArgument() {
    }

    @Parser(name = "", suggestions = "action_trigger")
    public ActionTrigger parse(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString();
        final @Nullable ActionTrigger trigger = ActionTrigger.getByName(value);
        if (trigger == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_action_trigger").replaceStripped("input", value).send(context.sender()));
        return trigger;
    }

    @Suggestions("action_trigger")
    public List<String> suggestions(final CommandContext<CommandSender> context, final CommandInput input) {
        return List.of(
                ActionTrigger.ANY_CLICK.name().toLowerCase(),
                ActionTrigger.LEFT_CLICK.name().toLowerCase(),
                ActionTrigger.RIGHT_CLICK.name().toLowerCase()
        );
    }
}
