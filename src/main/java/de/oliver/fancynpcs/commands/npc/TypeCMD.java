package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public enum TypeCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc type")
    @Permission("fancynpcs.command.npc.type")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_type_syntax").send(sender);
    }

    @Command("npc type <npc> <type>")
    @Permission("fancynpcs.command.npc.type")
    public boolean onCommand(final CommandSender sender, final Npc npc, final EntityType type) {
        // Calling the event and updating the type if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TYPE, type, sender).callEvent()) {
            npc.getData().setType(type);
            if (type != EntityType.PLAYER) {
                npc.getData().setGlowing(false);
                npc.getData().setShowInTab(false);
                if (npc.getData().getEquipment() != null) {
                    npc.getData().getEquipment().clear();
                }
            }

            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            //MessageHelper.success(receiver, lang.get("npc-command-type-updated", "npc", npc.getData().getName(), "type", type.name().toLowerCase()));
        } else {
            //MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
