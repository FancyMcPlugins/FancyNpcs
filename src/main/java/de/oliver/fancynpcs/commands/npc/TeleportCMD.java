package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class TeleportCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();
    private final DecimalFormat DF = new DecimalFormat(".###");

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        RayTraceResult rayTraceResult = player.rayTraceBlocks(50);

        if (args.length == 3) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    DF.format(rayTraceResult.getHitPosition().getX()) :
                    DF.format(player.getLocation().getX())).replace(',', '.')
            );
        } else if (args.length == 4) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    DF.format(rayTraceResult.getHitPosition().getY()) :
                    DF.format(player.getLocation().getY())).replace(',', '.')
            );
        } else if (args.length == 5) {
            return List.of(String.valueOf(rayTraceResult != null ?
                    DF.format(rayTraceResult.getHitPosition().getZ()) :
                    DF.format(player.getLocation().getZ())).replace(',', '.')
            );
        } else if (args.length == 6) {
            return List.of(player.getLocation().getWorld().getName());
        }

        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (args.length < 5) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        double x, y, z;

        try {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            MessageHelper.error(receiver, "wrong-usage");
            MessageHelper.error(receiver, "could-not-parse-number");
            return false;
        }

        World world = null;

        if (args.length == 6) {
            world = Bukkit.getWorld(args[5]);
        } else if (receiver instanceof Player p) {
            world = p.getWorld();
        }

        if (world == null) {
            MessageHelper.error(receiver, "world-not-found");
            return false;
        }

        Location location = new Location(world, x, y, z);

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setLocation(location);
            npc.updateForAll();
            MessageHelper.success(receiver, lang.get("npc-command-teleport-success"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
