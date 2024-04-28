package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public enum EquipmentCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc equipment", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.equipment")
    public void onDefault(final Player sender) {
        translator.translate("npc_equipment_syntax").send(sender);
    }

    @Command(value = "npc equipment <npc> <slot>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.equipment")
    // NOTE: NpcEquipmentSlot and potentially other enums could use their own parser to keep the error message (somewhat) accurate.
    public void onCommand(final Player sender, final Npc npc, final NpcEquipmentSlot slot) {
        // Getting item player has currently in hand.
        final ItemStack item = sender.getInventory().getItemInMainHand().clone();
        // Calling the event and updating equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{slot, item}, sender).callEvent()) {
            npc.getData().addEquipment(slot, item);
            npc.updateForAll();
            translator.translate("npc_equipment_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
