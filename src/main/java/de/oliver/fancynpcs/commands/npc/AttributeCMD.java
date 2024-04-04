package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.AttributeManagerImpl;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final AttributeManagerImpl attributeManager = FancyNpcs.getInstance().getAttributeManager();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            return null;
        }

        if (args.length == 3) {
            List<NpcAttribute> attributes = attributeManager.getAllAttributesForEntityType(npc.getData().getType());

            return attributes.stream()
                    .map(NpcAttribute::getName)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 4) {
            String attributeName = args[2];
            NpcAttribute attribute = attributeManager.getAttributeByName(npc.getData().getType(), attributeName);
            if (attribute == null) {
                return null;
            }

            return attribute.getPossibleValues().stream()
                    .filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        if (args.length < 4) {
            translator.translate("npc_attribute_syntax").send(sender);
            return false;
        }

        String attributeName = args[2].toLowerCase(); // note: forced lower-case for better command output
        String value = "";

        for (int i = 3; i < args.length; i++) {
            value += args[i] + " ";
        }
        value = value.substring(0, value.length() - 1);

        NpcAttribute attribute = attributeManager.getAttributeByName(npc.getData().getType(), attributeName);
        if (attribute == null) {
            translator.translate("npc_attribute_invalid_attribute").replace("input", attributeName).send(sender);
            return false;
        }

        if (!attribute.getTypes().contains(npc.getData().getType())) {
            translator.translate("npc_attribute_invalid_entity_type").replace("input", attributeName).send(sender);
            return false;
        }

        if (!attribute.isValidValue(value)) {
            translator.translate("npc_attribute_invalid_attribute_value").replace("input", value).send(sender);
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ATTRIBUTE, new Object[]{attribute, value}, sender);
        npcModifyEvent.callEvent();

        if (npcModifyEvent.isCancelled()) {
            translator.translate("command_npc_modification_cancelled").send(sender);
            return false;
        }

        npc.getData().addAttribute(attribute, value);
        npc.updateForAll();

        translator.translate("npc_attribute_set").replace("attribute", attributeName).replace("value", value).send(sender);

        return false;
    }
}
