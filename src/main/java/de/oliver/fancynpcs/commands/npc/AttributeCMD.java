package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.AttributeManagerImpl;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.Collections;
import java.util.List;

public enum AttributeCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final AttributeManagerImpl attributeManager = FancyNpcs.getInstance().getAttributeManager();

    @Suggestions("attribute_value")
    public List<String> attributeValueSuggestions(final CommandContext<CommandSender> context, final CommandInput input) {
        final Npc npc = context.getOrDefault("npc", null);
        final NpcAttribute attribute = context.getOrDefault("attribute", null);
        // ...
        if (attribute != null && npc != null)
            return attributeManager.getAttributeByName(npc.getData().getType(), attribute.getName()).getPossibleValues();
        // ...
        return Collections.emptyList();
    }

    @Command("npc attribute <npc> <attribute> <attributeValue>")
    @Permission("fancynpcs.command.npc.attribute")
    public void onCommand(
            final CommandSender sender,
            final Npc npc,
            final NpcAttribute attribute,
            final @Argument(suggestions = "attribute_value") String attributeValue
    ) {
        if (!attribute.isValidValue(attributeValue)) {
            translator.translate("npc_attribute_invalid_attribute_value").replace("input", attributeValue).send(sender);
            return;
        }
        // ...
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ATTRIBUTE, new Object[]{attribute, attributeValue}, sender).callEvent()) {
            npc.getData().addAttribute(attribute, attributeValue);
            npc.updateForAll();
            translator.translate("npc_attribute_set").replace("attribute", attribute.getName()).replace("value", attributeValue.toLowerCase()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
