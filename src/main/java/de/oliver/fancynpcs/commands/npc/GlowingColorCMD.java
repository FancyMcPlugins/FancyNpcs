package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowingColorCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length == 3) {
            return NamedTextColor.NAMES.keys().stream()
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            translator.translate("npc_glowingColor_syntax").send(sender);
            return false;
        }


        if (npc == null) {
            translator.translate("command_invalid_npc").send(sender);
            return false;
        }

        NamedTextColor color = NamedTextColor.NAMES.value(args[2]);
        if (color == null) {
            translator.translate("npc_glowingColor_failure_invalid_color").replace("input", args[2]).send(sender);
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color, sender);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setGlowingColor(color);
            npc.updateForAll();
            translator.translate("npc_glowingColor_failure_invalid_color").replace("input", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return true;
    }
}
