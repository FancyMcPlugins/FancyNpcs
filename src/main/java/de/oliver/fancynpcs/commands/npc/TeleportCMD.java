package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum TeleportCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc teleport <npc>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.teleport")
    public void onTeleport(
            final @NotNull Player sender,
            final @NotNull Npc npc
    ) {
        final Location location = npc.getData().getLocation();
        // Checking if the world is still loaded.
        if (location.getWorld() == null) {
            translator.translate("npc_teleport_failure_world_not_loaded").send(sender);
            return;
        }
        // Teleporting and sending message to the sender. This operation can occasionally fail.
        sender.teleportAsync(location).whenComplete((isSuccess, thr) -> {
            translator.translate(isSuccess ? "npc_teleport_success" : "npc_teleport_failure_exception").replace("npc", npc.getData().getName()).send(sender);
            // Printing stacktrace to the console in case an exception occurred.
            if (thr != null)
                thr.printStackTrace();
        });
    }

}
