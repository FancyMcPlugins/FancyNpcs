package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;

import org.jetbrains.annotations.Nullable;

public enum TeleportCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    @Command("npc teleport")
    @Permission("fancynpcs.command.npc.teleport")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_teleport_syntax").send(sender);
    }

    @Command("npc teleport <npc> <location> [world]")
    @Permission("fancynpcs.command.npc.teleport")
    public void onCommand(final CommandSender sender, final Npc npc, final Location location, final @Nullable World world) {

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender).callEvent()) {
            npc.getData().setLocation(location);
            npc.updateForAll();
            translator.translate("command_invalid_world")
                    .replace("npc", npc.getData().getName())
                    .replace("x", COORDS_FORMAT.format(location.x()))
                    .replace("y", COORDS_FORMAT.format(location.y()))
                    .replace("z", COORDS_FORMAT.format(location.z()))
                    .replace("world", world.getName())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
