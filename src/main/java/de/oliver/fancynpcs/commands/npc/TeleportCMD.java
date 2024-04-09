package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        RayTraceResult rayTraceResult = player.rayTraceBlocks(50);

        if (args.length == 3) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    COORDS_FORMAT.format(rayTraceResult.getHitPosition().getX()) :
                    COORDS_FORMAT.format(player.getLocation().getX())).replace(',', '.')
            );
        } else if (args.length == 4) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    COORDS_FORMAT.format(rayTraceResult.getHitPosition().getY()) :
                    COORDS_FORMAT.format(player.getLocation().getY())).replace(',', '.')
            );
        } else if (args.length == 5) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    COORDS_FORMAT.format(rayTraceResult.getHitPosition().getZ()) :
                    COORDS_FORMAT.format(player.getLocation().getZ())).replace(',', '.')
            );
        } else if (args.length == 6) {
            return List.of(player.getLocation().getWorld().getName());
        }

        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        if (args.length < 5) {
            translator.translate("npc_teleport_syntax").send(sender);
            return false;
        }

        double x, y, z;

        try { // NOTE: Additional try-catch blocks has been added to improve error message, will be improved during commands rework.
            x = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            translator.translate("command_invalid_number").replace("input", args[2]).send(sender);
            return false;
        }

        try { // NOTE: Additional try-catch blocks has been added to improve error message, will be improved during commands rework.
            y = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            translator.translate("command_invalid_number").replace("input", args[3]).send(sender);
            return false;
        }

        try { // NOTE: Additional try-catch blocks has been added to improve error message, will be improved during commands rework.
            z = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            translator.translate("command_invalid_number").replace("input", args[4]).send(sender);
            return false;
        }

        World world = null;

        if (args.length == 6) {
            world = Bukkit.getWorld(args[5]);
        } else if (sender instanceof Player p) {
            world = p.getWorld();
        }

        if (world == null) {
            translator.translate("command_invalid_world").replace("input", args[5]).send(sender);
            return false;
        }

        Location location = new Location(world, x, y, z);

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setLocation(location);
            npc.updateForAll();
            translator.translate("command_invalid_world")
                    .replace("npc", npc.getData().getName())
                    .replace("x", COORDS_FORMAT.format(x))
                    .replace("y", COORDS_FORMAT.format(x))
                    .replace("z", COORDS_FORMAT.format(x))
                    .replace("world", world.getName())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return true;
    }
}
