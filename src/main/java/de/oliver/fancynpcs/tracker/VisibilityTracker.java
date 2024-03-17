package de.oliver.fancynpcs.tracker;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VisibilityTracker implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
                npc.checkAndUpdateVisibility(player);
            }
        }
    }
}
