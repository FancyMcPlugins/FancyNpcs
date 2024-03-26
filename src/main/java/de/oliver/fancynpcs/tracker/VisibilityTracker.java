package de.oliver.fancynpcs.tracker;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VisibilityTracker implements Runnable {

    private Set<UUID> joinDelayPlayers;

    public VisibilityTracker() {
        this.joinDelayPlayers = new HashSet<>();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (joinDelayPlayers.contains(player.getUniqueId())) {
                continue;
            }

            for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
                npc.checkAndUpdateVisibility(player);
            }
        }
    }

    public void addJoinDelayPlayer(UUID player) {
        joinDelayPlayers.add(player);
    }

    public void removeJoinDelayPlayer(UUID player) {
        joinDelayPlayers.remove(player);
    }
}
