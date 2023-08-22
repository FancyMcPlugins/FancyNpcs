package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            MessageHelper.error(player, lang.get("npc_commands-not_found"));
            return false;
        }

        NpcRemoveEvent npcRemoveEvent = new NpcRemoveEvent(npc, player);
        npcRemoveEvent.callEvent();
        if (!npcRemoveEvent.isCancelled()) {
            npc.removeForAll();
            // Iterating over all online players that the NPC is currently looking at.
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (npc.getIsLookingAtPlayer().getOrDefault(onlinePlayer.getUniqueId(), false)) {
                    // Changing state as Npc#getIsLookingAtPlayer#get(...) called within the event listener should return false now.
                    npc.getIsLookingAtPlayer().put(onlinePlayer.getUniqueId(), false);
                    // Calling the NpcStopLookingEvent event.
                    new NpcStopLookingEvent(npc, onlinePlayer).callEvent();
                }
            }
            FancyNpcs.getInstance().getNpcManagerImpl().removeNpc(npc);
            MessageHelper.success(player, lang.get("npc_commands-remove-removed"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-remove-failed"));
        }

        return false;
    }
}
