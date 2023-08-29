package de.oliver.fancynpcs.listeners;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Comparator;
import java.util.Map;

public class PlayerNpcsListener implements Listener {

    private static final boolean isUsingPlotSquared = FancyNpcs.getInstance().isUsingPlotSquared();

    @EventHandler
    public void onNpcCreate(NpcCreateEvent event) {
        Player p = event.getPlayer();
        if (isUsingPlotSquared) {
            PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayer(p.getUniqueId());
            Plot currentPlot = plotPlayer.getCurrentPlot();
            if ((currentPlot == null || !currentPlot.isOwner(p.getUniqueId())) && !p.hasPermission("fancynpcs.admin")) {
                MessageHelper.error(p, "You are only allowed to create npcs on your plot");
                event.setCancelled(true);
                return;
            }
        }
        int maxNpcs = FancyNpcs.getInstance().getFancyNpcConfig().getMaxNpcsPerPermission()
                .entrySet().stream()
                .filter(entry -> p.hasPermission(entry.getKey()))
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getValue)
                .orElse(Integer.MAX_VALUE);

        int npcAmount = 0;
        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            if (npc.getData().getCreator().equals(p.getUniqueId()))
                npcAmount++;
        }
        if (npcAmount >= maxNpcs && !p.hasPermission("fancynpcs.admin")) {
            MessageHelper.error(p, "You have reached the maximum amount of npcs");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onNpcRemove(NpcRemoveEvent event) {
        Player p = event.getPlayer();
        if (!event.getNpc().getData().getCreator().equals(p.getUniqueId()) && !p.hasPermission("fancynpcs.admin")) {
            MessageHelper.error(p, "You can only modify your npcs");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onNpcModify(NpcModifyEvent event) {
        Player p = event.getPlayer();

        if (!event.getNpc().getData().getCreator().equals(p.getUniqueId()) && !p.hasPermission("fancynpcs.admin")) {
            MessageHelper.error(p, "You can only modify your npcs");
            event.setCancelled(true);
            return;
        }
        if (isUsingPlotSquared && event.getModification() == NpcModifyEvent.NpcModification.LOCATION) {
            PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayer(p.getUniqueId());
            Plot currentPlot = plotPlayer.getCurrentPlot();

            if ((currentPlot == null || !currentPlot.isOwner(p.getUniqueId())) && !p.hasPermission("fancynpcs.admin")) {
                MessageHelper.error(p, "You are only allowed to teleport npcs on your plot");
                event.setCancelled(true);
            }
        }
    }
}
