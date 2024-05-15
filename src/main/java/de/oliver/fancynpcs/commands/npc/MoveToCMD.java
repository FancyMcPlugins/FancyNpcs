package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MoveToCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    @Command("npc move_to <npc> <location> [world]")
    @Permission("fancynpcs.command.npc.move_to")
    public void onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "relative_location") Location location,
            final @Nullable World world,
            final @Flag("look-in-my-direction") boolean shouldLookInSenderDirection
    ) {
        // Finalizing World argument. Player-like senders don't have to specify the 'world' argument which then defaults to the World sender is currently in.
        final World finalWorld = (world == null && sender instanceof Player player) ? player.getWorld() : world;
        // Sending error message if finalized World argument ended up being null. This can happen when command is executed by console and 'world' argument was not specified.
        if (finalWorld == null) {
            translator.translate("npc_move_to_failure_must_specify_world").send(sender);
            return;
        }
        // Updating World of the finalized Location. This should never pass a null value.
        location.setWorld(finalWorld);
        // Updating direction NPC will be looking at. Only if '--look-in-my-direction' is present and sender is player.
        if (shouldLookInSenderDirection && sender instanceof Player player)
            location.setDirection(player.getLocation().subtract(location).toVector());
        // Calling the event and re-locating NPC if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender).callEvent()) {
            npc.getData().setLocation(location);
            npc.updateForAll();
            translator.translate("npc_move_to_success")
                    .replace("npc", npc.getData().getName())
                    .replace("x", COORDS_FORMAT.format(location.x()))
                    .replace("y", COORDS_FORMAT.format(location.y()))
                    .replace("z", COORDS_FORMAT.format(location.z()))
                    .replace("world", finalWorld.getName())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
