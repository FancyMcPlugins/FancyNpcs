package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.AttributeManager;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public enum AttributeCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final AttributeManager attributeManager = FancyNpcs.getInstance().getAttributeManager();

    @Command("npc attribute <npc> set <attribute> <attributeValue>")
    @Permission("fancynpcs.command.npc.attribute.set")
    public void onAttributeSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull NpcAttribute attribute,
            final @NotNull @Argument(parserName = "AttributeCMD/attribute_value") String attributeValue
    ) {
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ATTRIBUTE, new Object[]{attribute, attributeValue}, sender).callEvent()) {
            npc.getData().addAttribute(attribute, attributeValue);
            npc.updateForAll();
            translator.translate("npc_attribute_set").replace("attribute", attribute.getName()).replaceStripped("value", attributeValue.toLowerCase()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc attribute <npc> list")
    @Permission("fancynpcs.command.npc.attribute.list")
    public void onAttributeList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Sending error message if the list is empty.
        if (npc.getData().getAttributes().isEmpty()) {
            translator.translate("npc_attribute_list_failure_empty").send(sender);
            return;
        }
        translator.translate("npc_attribute_list_header").send(sender);
        // Iterating over all attributes set on this NPC and sending them to the sender.
        npc.getData().getAttributes().forEach((attribute, value) -> {
            translator.translate("npc_attribute_list_entry")
                    .replace("attribute", attribute.getName())
                    .replace("value", value)
                    .send(sender);
        });
        translator.translate("npc_attribute_list_footer").send(sender);
    }

    /* PARSERS AND SUGGESTIONS */

    // This parser does not specify a name, making it default parser for the returned type.
    @Parser(name = "", suggestions = "AttributeCMD/attribute")
    public NpcAttribute parseAttribute(final CommandContext<CommandSender> context, final CommandInput input) {
        // Getting the 'npc' argument that should already exist within the command context.
        final Npc npc = context.get("npc");
        // Reading the string, which is supposed to be an attribute name.
        final String value = input.readString();
        // Getting the NpcAttribute from the name and npc type.
        final NpcAttribute attribute = attributeManager.getAttributeByName(npc.getData().getType(), value);
        // Throwing exception when non-existent attribute has been provided.
        if (attribute == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_attribute").replaceStripped("input", value).send(context.sender()));
        // Otherwise, returning the attribute from the parser.
        return attribute;
    }

    @Parser(name = "AttributeCMD/attribute_value", suggestions = "AttributeCMD/attribute_value")
    public String parseAttributeValue(final CommandContext<CommandSender> context, final CommandInput input) {
        // Getting the 'attribute' argument that should already exist within the command context.
        final NpcAttribute attribute = context.get("attribute");
        // Reading the string, which is supposed to be an attribute name.
        final String value = input.read(input.remainingLength());
        // Sending error message if attribute is null or cannot accept provided value.
        if (!attribute.isValidValue(value))
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_attribute_value").replaceStripped("input", value).send(context.sender()));
        // Otherwise, returning the attribute from the parser.
        return value;
    }

    @Suggestions("AttributeCMD/attribute")
    public List<String> suggestAttribute(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.getOrDefault("npc", null);
        return attributeManager.getAllAttributesForEntityType(npc.getData().getType()).stream().map(NpcAttribute::getName).toList();
    }

    @Suggestions("AttributeCMD/attribute_value")
    public List<String> suggestAttributeValue(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.get("npc");
        final NpcAttribute attribute = context.get("attribute");
        return attributeManager.getAttributeByName(npc.getData().getType(), attribute.getName()).getPossibleValues();
    }

}
