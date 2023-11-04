package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShowInTabCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }


        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (npc.getData().getType() != EntityType.PLAYER) {
            MessageHelper.error(receiver, lang.get("npc-must-be-player"));
            return false;
        }

        boolean showInTab;
        switch (args[2].toLowerCase()) {
            case "true" -> showInTab = true;
            case "false" -> showInTab = false;
            default -> {
                MessageHelper.error(receiver, lang.get("npc-command-showInTab-invalid-argument"));
                return false;
            }
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, showInTab, player);
        npcModifyEvent.callEvent();

        if (showInTab == npc.getData().isShowInTab()) {
            MessageHelper.warning(receiver, lang.get("npc-command-showInTab-same"));
            return false;
        }

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setShowInTab(showInTab);
            npc.updateForAll();

            if (showInTab) {
                MessageHelper.success(receiver, lang.get("npc-command-showInTab-true"));
            } else {
                MessageHelper.success(receiver, lang.get("npc-command-showInTab-false"));
            }
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
