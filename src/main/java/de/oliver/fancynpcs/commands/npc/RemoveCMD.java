package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        NpcRemoveEvent npcRemoveEvent = new NpcRemoveEvent(npc, sender);
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
            translator.translate("npc_remove_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return false;
    }
}
