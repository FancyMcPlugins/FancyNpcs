package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum RemoveCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc remove <npc>")
    @Permission("fancynpcs.command.npc.remove")
    public void onRemove(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        // Calling the event and removing the NPC if not cancelled.
        if (new NpcRemoveEvent(npc, sender).callEvent()) {
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
            translator.translate("npc_remove_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
