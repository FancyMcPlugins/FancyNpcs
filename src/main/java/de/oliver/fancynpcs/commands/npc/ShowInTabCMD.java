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
import java.util.stream.Stream;

public class ShowInTabCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length == 3) {
            return Stream.of("true", "false")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

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
                MessageHelper.error(receiver, lang.get("npc-command-showInTab-invalid-argument", "input", args[2].toLowerCase()));
                return false;
            }
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, showInTab, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setShowInTab(showInTab);

            if (showInTab) {
                npc.updateForAll();
            } else {
                npc.removeForAll();
                npc.spawnForAll();
            }

            if (showInTab) {
                MessageHelper.success(receiver, lang.get("npc-command-showInTab-true", "npc", npc.getData().getName()));
            } else {
                MessageHelper.success(receiver, lang.get("npc-command-showInTab-false", "npc", npc.getData().getName()));
            }
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
