package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WaitAction extends NpcAction {

    public WaitAction() {
        super("wait", true);
    }

    /**
     * Executes the "wait" action for an NPC.
     *
     * @param npc    The NPC on which to execute the action.
     * @param player The player triggering the action.
     * @param value  The value representing the time to wait in seconds.
     */
    @Override
    public void execute(@NotNull Npc npc, Player player, String value) {
        int time;
        try {
            time = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            FancyNpcsPlugin.get().getLogger().warning("Invalid time value for wait action: " + value);
            return;
        }

        player.sendMessage("Waiting for " + time + " seconds...");

        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            FancyNpcsPlugin.get().getLogger().warning("Thread was interrupted while waiting");
        }

        player.sendMessage("Finished waiting for " + time + " seconds.");
    }
}
