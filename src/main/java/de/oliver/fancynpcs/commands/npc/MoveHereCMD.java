package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum MoveHereCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc move_here <npc>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.move_here")
    public void onCommand(
            final @NotNull Player sender,
            final @NotNull Npc npc
    ) {
        final Location location = sender.getLocation();
        final String oldWorld = npc.getData().getLocation().getWorld().getName();
        // Calling the event and moving the NPc to location of the sender, if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender).callEvent()) {
            npc.getData().setLocation(location);
            if (oldWorld.equals(location.getWorld().getName())) {
                npc.updateForAll();
            } else {
                npc.removeForAll();
                npc.spawnForAll();
            }
            translator.translate("npc_move_here_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
