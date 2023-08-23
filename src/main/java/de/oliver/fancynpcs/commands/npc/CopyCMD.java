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

public class CopyCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            MessageHelper.error(player, lang.get("npc_commands-not_found"));
            return false;
        }

        if (args.length < 3) {
            MessageHelper.error(player, lang.get("npc_commands-wrong_usage"));
            return false;
        }

        String newName = args[2];

        Npc copied = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(
                newName,
                npc.getData().getDisplayName(),
                npc.getData().getSkin(),
                player.getLocation(),
                npc.getData().isShowInTab(),
                npc.getData().isSpawnEntity(),
                npc.getData().isGlowing(),
                npc.getData().getGlowingColor(),
                npc.getData().getType(),
                npc.getData().getEquipment(),
                npc.getData().isTurnToPlayer(),
                npc.getData().getOnClick(),
                npc.getData().getMessage(),
                npc.getData().getServerCommand(),
                npc.getData().getPlayerCommand(),
                npc.getData().getAttributes()
        ));

        NpcCreateEvent npcCreateEvent = new NpcCreateEvent(copied, player);
        npcCreateEvent.callEvent();
        if (!npcCreateEvent.isCancelled()) {
            copied.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();

            MessageHelper.success(player, lang.get("npc_commands-copy-success"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-copy-failed"));
        }

        return true;
    }
}
