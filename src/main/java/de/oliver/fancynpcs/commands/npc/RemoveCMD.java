package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.commands.Subcommand;
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
            FancyNpcs.getInstance().getNpcManagerImpl().removeNpc(npc);
            MessageHelper.success(player, lang.get("npc_commands-remove-removed"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-remove-failed"));
        }

        return false;
    }
}
