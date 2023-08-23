package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlowingColorCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(player, lang.get("npc_commands-wrong_usage"));
            return false;
        }


        if (npc == null) {
            MessageHelper.error(player, lang.get("npc_commands-not_found"));
            return false;
        }

        NamedTextColor color = NamedTextColor.NAMES.value(args[2]);
        if (color == null) {
            MessageHelper.error(player, lang.get("npc_commands-glowingColor-invalid"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setGlowingColor(color);
            npc.updateForAll();
            MessageHelper.success(player, lang.get("npc_commands-glowingColor-updated"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-glowingColor-failed"));
        }

        return true;
    }
}
