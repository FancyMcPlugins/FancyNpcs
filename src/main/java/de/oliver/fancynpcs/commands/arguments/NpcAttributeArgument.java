package de.oliver.fancynpcs.commands.arguments;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.AttributeManager;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;

public enum NpcAttributeArgument {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final AttributeManager attributeManager = FancyNpcs.getInstance().getAttributeManager();

    @Parser(suggestions = "attribute")
    public NpcAttribute parser(final CommandContext<CommandSender> context, final CommandInput input) {
        // Getting npc argument that already should exist within the command context.
        final Npc npc = context.get("npc");
        // Reading the string, which is supposed to be an attribute name.
        final String value = input.readString();
        // Getting the NpcAttribute from the name and npc type.
        final NpcAttribute attribute = attributeManager.getAttributeByName(npc.getData().getType(), value);
        // Throwing exception when non-existent attribute has been provided.
        if (attribute == null)
            throw ReplyingParseException.replying(() -> translator.translate("npc_attribute_invalid_for_this_entity_type").send(context.sender()));
        // Otherwise, returning the attribute from the parser.
        return attribute;
    }

    @Suggestions("attribute")
    public List<String> suggestions(final CommandContext<CommandSender> context, final CommandInput input) {
        // Getting npc argument that already should exist within the command context.
        final Npc npc = context.getOrDefault("npc", null);
        // Mapping and returning list of suggestions.
        return attributeManager.getAllAttributesForEntityType(npc.getData().getType()).stream().map(NpcAttribute::getName).toList();
    }

}
