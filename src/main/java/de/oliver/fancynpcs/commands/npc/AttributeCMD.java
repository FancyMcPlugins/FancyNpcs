package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.AttributeManagerImpl;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AttributeCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();
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
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (args.length < 4) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        String attributeName = args[2];
        String value = "";

        for (int i = 3; i < args.length; i++) {
            value += args[i] + " ";
        }
        value = value.substring(0, value.length() - 1);

        NpcAttribute attribute = attributeManager.getAttributeByName(npc.getData().getType(), attributeName);
        if (attribute == null) {
            MessageHelper.error(receiver, lang.get("npc-command-attribute-attribute-not-found"));
            return false;
        }

        if (!attribute.getTypes().contains(npc.getData().getType())) {
            MessageHelper.error(receiver, lang.get("npc-command-attribute-wrong-entity-type"));
            return false;
        }

        if (!attribute.isValidValue(value)) {
            MessageHelper.error(receiver, lang.get("npc-command-attribute-invalid-value"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ATTRIBUTE, new Object[]{attribute, value}, player);
        npcModifyEvent.callEvent();

        if (npcModifyEvent.isCancelled()) {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
            return false;
        }

        npc.getData().addAttribute(attribute, value);
        npc.updateForAll();

        MessageHelper.success(receiver, lang.get("npc-command-attribute-success"));

        return false;
    }
}
