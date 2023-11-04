package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlowingCMD implements Subcommand {

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

        boolean glowing;
        try {
            glowing = Boolean.parseBoolean(args[2]);
        } catch (Exception e) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, glowing, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setGlowing(glowing);
            npc.updateForAll();

            if (glowing) {
                MessageHelper.success(receiver, lang.get("npc-command-glowing-true"));
            } else {
                MessageHelper.success(receiver, lang.get("npc-command-glowing-false"));
            }
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
