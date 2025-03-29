package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

public enum CenterCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc center <npc>")
    @Permission("fancynpcs.command.npc.center")
    public void onCenter(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        NpcData npcData = npc.getData();
        Location location = npcData.getLocation();
        
        if (location == null) {
            translator.translate("npc_center_failure_no_location").replace("npc", npcData.getName()).send(sender);
            return;
        }
        
        // Center the NPC on the block
        Location centeredLocation = location.clone();
        centeredLocation.setX(centeredLocation.getBlockX() + 0.5);
        centeredLocation.setY(centeredLocation.getY());
        centeredLocation.setZ(centeredLocation.getBlockZ() + 0.5);
        
        // Trigger the modify event
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, centeredLocation, sender).callEvent()) {
            npcData.setLocation(centeredLocation);
            npc.updateForAll();
            
            translator.translate("npc_center_success")
                    .replace("npc", npcData.getName())
                    .replace("x", String.format("%.2f", centeredLocation.getX()))
                    .replace("y", String.format("%.2f", centeredLocation.getY()))
                    .replace("z", String.format("%.2f", centeredLocation.getZ()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
} 