package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TypeCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(player, lang.get("wrong-usage"));
            return false;
        }

        if (npc == null) {
            MessageHelper.error(player, lang.get("npc-not-found"));
            return false;
        }

        EntityType type = EntityType.fromName(args[2].toLowerCase());

        if (type == null) {
            MessageHelper.error(player, lang.get("npc-command-type-invalid"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TYPE, type, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setType(type);

            if (type != EntityType.PLAYER) {
                npc.getData().setGlowing(false);
                npc.getData().setShowInTab(false);
                if (npc.getData().getEquipment() != null) {
                    npc.getData().getEquipment().clear();
                }
            }

            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            MessageHelper.success(player, lang.get("npc-command-type-updated"));
        } else {
            MessageHelper.error(player, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
