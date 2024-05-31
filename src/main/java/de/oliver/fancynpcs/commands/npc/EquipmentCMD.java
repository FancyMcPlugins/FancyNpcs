package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EquipmentCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private static final List<String> SLOT_SUGGESTIONS = Arrays.stream(NpcEquipmentSlot.values()).map(slot -> slot.name().toLowerCase()).toList();
    // Replace with Registry#stream after dropping 1.19.4 support.
    private static final List<String> MATERIAL_SUGGESTIONS = StreamSupport.stream(Registry.MATERIAL.spliterator(), false).filter(Material::isItem).map(material -> material.key().asString()).toList();

    @Command("npc equipment <npc> set <slot> <item>")
    @Permission("fancynpcs.command.npc.equipment.set")
    public void onEquipmentSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull NpcEquipmentSlot slot,
            final @NotNull @Argument(parserName = "EquipmentCMD/item") ItemStack item
    ) {
        // Calling the event and updating equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{slot, item}, sender).callEvent()) {
            npc.getData().addEquipment(slot, item);
            npc.updateForAll();
            translator.translate(item.getType() != Material.AIR ? "npc_equipment_set_item" : "npc_equipment_set_empty")
                    .replace("npc", npc.getData().getName())
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", (item.getType() != Material.AIR) ? item.displayName().hoverEvent(item.asHoverEvent()) : Component.empty()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc equipment <npc> clear")
    @Permission("fancynpcs.command.npc.equipment.clear")
    public void onEquipmentClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Calling the event and clearing equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, null, sender).callEvent()) {
            // Entries must be set to null manually because clearing the map would prevent equipment from being updated. (Npc#update checks if map is empty)
            for (final NpcEquipmentSlot slot : NpcEquipmentSlot.values())
                npc.getData().getEquipment().put(slot, null);
            npc.updateForAll();
            translator.translate("npc_equipment_clear_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc equipment <npc> list")
    @Permission("fancynpcs.command.npc.equipment.list")
    public void onEquipmentList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Sending error message if the list is empty or all items are Material.AIR.
        if (npc.getData().getEquipment().isEmpty() || npc.getData().getEquipment().values().stream().allMatch(item -> item == null || item.getType() == Material.AIR)) {
            translator.translate("npc_equipment_list_failure_empty").send(sender);
            return;
        }
        translator.translate("npc_equipment_list_header").send(sender);
        // Iterating over all equipment slots of this NPC and sending them to the sender.
        npc.getData().getEquipment().forEach((slot, item) -> {
            // Skipping null entries and Material.AIR, no need to display that.
            if (item == null || item.getType() == Material.AIR)
                return;
            translator.translate("npc_equipment_list_entry")
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", item.displayName().hoverEvent(item.asHoverEvent())))
                    .send(sender);
        });
        translator.translate("npc_equipment_list_footer").send(sender);
    }

    /* PARSERS AND SUGGESTIONS */

    // This parser does not specify a name, making it default parser for the returned type.
    @Parser(name = "", suggestions = "EquipmentCMD/slot")
    public NpcEquipmentSlot parseSlot(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString().toLowerCase();
        final @Nullable NpcEquipmentSlot slot = NpcEquipmentSlot.parse(value);
        // Sending error message if input is not a valid NpcEquipmentSlot.
        if (slot == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_equipment_slot").replaceStripped("input", value).send(context.sender()));
        return slot;
    }

    @Parser(name = "EquipmentCMD/item", suggestions = "EquipmentCMD/item")
    public ItemStack parseItem(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString().toLowerCase();
        // Handling '@none', which returns air (and effectively disables)
        if (value.equals("@none"))
            return new ItemStack(Material.AIR);
        // Handling '@hand', which returns item player currently have in their hand.
        else if (value.equals("@hand") && context.sender() instanceof Player player)
            return player.getInventory().getItemInMainHand().clone();
        // Otherwise, trying to parse input as an material.
        else {
            // Converting input to NamespacedKey. Defaults to 'minecraft:' namespace if missing from input.
            final @Nullable NamespacedKey key = NamespacedKey.fromString(value);
            // Sending error message if input is not a valid NamespacedKey.
            if (key == null)
                throw ReplyingParseException.replying(() -> translator.translate("command_invalid_material").replaceStripped("input", value).send(context.sender()));
            // Getting material from the registry.
            final @Nullable Material material = Registry.MATERIAL.get(key);
            // Sending error message if no material was found.
            if (material == null || !material.isItem())
                throw ReplyingParseException.replying(() -> translator.translate("command_invalid_material").replaceStripped("input", value).send(context.sender()));
            // Returning new ItemStack object from the specified Material.
            return new ItemStack(material);
        }
    }

    @Suggestions("EquipmentCMD/item")
    public List<String> suggestItem(final CommandContext<CommandSender> context, final CommandInput input) {
        return new ArrayList<>(MATERIAL_SUGGESTIONS) {{
            // Adding '@none' placeholder which is replaced with 'minecraft:air'.
            add("@none");
            // If applicable, adding '@hand' placeholder which is replaced with item player currently have in their hand.
            if (context.sender() instanceof Player)
                add("@hand");
        }};
    }

    @Suggestions("EquipmentCMD/slot")
    public List<String> suggestSlot(final CommandContext<CommandSender> context, final CommandInput input) {
        return SLOT_SUGGESTIONS;
    }

    /* UTILITY METHODS */

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedSlot(final @NotNull NpcEquipmentSlot slot) {
        return ((SimpleMessage) translator.translate(
                switch (slot) {
                    case MAINHAND -> "main_hand";
                    case OFFHAND -> "off_hand";
                    case HEAD -> "head";
                    case CHEST -> "chest";
                    case LEGS -> "legs";
                    case FEET -> "feet";
                }
        )).getMessage();
    }

}
