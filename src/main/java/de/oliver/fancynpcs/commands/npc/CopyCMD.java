package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopyCMD implements Subcommand {

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

        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        if (args.length < 3) {
            translator.translate("npc_copy_syntax").send(sender);
            return false;
        }

        String newName = args[2];

        Npc copied = FancyNpcs.getInstance().getNpcAdapter().apply(
                new NpcData(
                        UUID.randomUUID().toString(),
                        newName,
                        player.getUniqueId(),
                        npc.getData().getDisplayName(),
                        npc.getData().getSkin(),
                        player.getLocation(),
                        npc.getData().isShowInTab(),
                        npc.getData().isSpawnEntity(),
                        npc.getData().isCollidable(),
                        npc.getData().isGlowing(),
                        npc.getData().getGlowingColor(),
                        npc.getData().getType(),
                        npc.getData().getEquipment(),
                        npc.getData().isTurnToPlayer(),
                        npc.getData().getOnClick(),
                        npc.getData().getMessages(),
                        npc.getData().isSendMessagesRandomly(),
                        npc.getData().getServerCommand(),
                        npc.getData().getPlayerCommands(),
                        npc.getData().getInteractionCooldown(),
                        npc.getData().getAttributes(),
                        npc.getData().isMirrorSkin()
                ));

        NpcCreateEvent npcCreateEvent = new NpcCreateEvent(copied, player);
        npcCreateEvent.callEvent();
        if (!npcCreateEvent.isCancelled()) {
            copied.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();

            translator.translate("npc_command_copy_success").replace("npc", npc.getData().getName()).replace("new_name", copied.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }

        return true;
    }
}
