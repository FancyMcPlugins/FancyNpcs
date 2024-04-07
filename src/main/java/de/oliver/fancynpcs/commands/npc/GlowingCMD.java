package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowingCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

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
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            translator.translate("npc_glowing_syntax").send(sender);
            return false;
        }


        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        boolean glowing;
        try {
            glowing = Boolean.parseBoolean(args[2]);
        } catch (Exception e) {
            translator.translate("command_invalid_boolean").replace("input", args[2]).send(sender);
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, glowing, sender);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setGlowing(glowing);
            npc.updateForAll();
            translator.translate(glowing ? "npc_glowing_set_true" : "npc_glowing_set_false").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return true;
    }
}
