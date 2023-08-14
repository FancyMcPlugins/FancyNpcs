package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        String name = args[1];

        if (FancyNpcs.getInstance().getNpcManager().getNpc(name) != null) {
            MessageHelper.error(player, lang.get("npc_commands-create-exist"));
            return false;
        }

        Npc createdNpc = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(name, player.getLocation()));
        createdNpc.getData().setLocation(player.getLocation());

        NpcCreateEvent npcCreateEvent = new NpcCreateEvent(createdNpc, player);
        npcCreateEvent.callEvent();
        if (!npcCreateEvent.isCancelled()) {
            createdNpc.create();
            FancyNpcs.getInstance().getNpcManager().registerNpc(createdNpc);
            createdNpc.spawnForAll();

            MessageHelper.success(player, lang.get("npc_commands-create-created"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-create-failed"));
        }

        return true;
    }
}
