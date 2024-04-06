package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.translations.Translator;
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

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            translator.translate("command_player_only").send(sender);
            return false;
        }

        String name = args[1];

        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()) {
            if (FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, player.getUniqueId()) != null) {
                translator.translate("npc_create_failure_already_exists").replace("npc", name).send(sender);
                return false;
            }
        } else {
            if (FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name) != null) {
                translator.translate("npc_create_failure_already_exists").replace("npc", name).send(sender);
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

            translator.translate("npc_create_success").replace("npc", name).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return true;
    }
}
