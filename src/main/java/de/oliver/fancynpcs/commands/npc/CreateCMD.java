package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (!(receiver instanceof Player player)) {
            MessageHelper.error(receiver, lang.get("npc-command.only-players"));
            return false;
        }

        String name = args[1];

        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()) {
            if (FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, player.getUniqueId()) != null) {
                MessageHelper.error(receiver, lang.get("npc-command-create-name-already-exists"));
                return false;
            }
        } else {
            if (FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name) != null) {
                MessageHelper.error(receiver, lang.get("npc-command-create-name-already-exists"));
                return false;
            }
        }


        if (name.contains(".")) {
            name = name.replace('.', '_');
        }

        Npc createdNpc = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(name, player.getUniqueId(), player.getLocation()));
        createdNpc.getData().setLocation(player.getLocation());

        NpcCreateEvent npcCreateEvent = new NpcCreateEvent(createdNpc, player);
        npcCreateEvent.callEvent();
        if (!npcCreateEvent.isCancelled()) {
            createdNpc.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(createdNpc);
            createdNpc.spawnForAll();

            MessageHelper.success(receiver, lang.get("npc-command-create-created"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-create-cancelled"));
        }

        return true;
    }
}
