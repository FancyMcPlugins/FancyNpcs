package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EquipmentCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(player, lang.get("npc_commands-wrong_usage"));
            return false;
        }


        if (npc == null) {
            MessageHelper.error(player, lang.get("npc_commands-not_found"));
            return false;
        }

        if (npc.getData().getType() != EntityType.PLAYER) {
            MessageHelper.error(player, lang.get("npc_commands-must_player"));
            return false;
        }

        String slot = args[2];

        NpcEquipmentSlot equipmentSlot = NpcEquipmentSlot.parse(slot);
        if (equipmentSlot == null) {
            MessageHelper.error(player, lang.get("npc_commands-equipment-invalid"));
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{equipmentSlot, item}, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().addEquipment(equipmentSlot, item);
            npc.updateForAll();
            MessageHelper.success(player, lang.get("npc_commands-equipment-updated"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-equipment-failed"));
        }

        return true;
    }
}
