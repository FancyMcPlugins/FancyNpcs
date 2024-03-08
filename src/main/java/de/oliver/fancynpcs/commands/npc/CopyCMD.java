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
import java.util.UUID;

public class CopyCMD implements Subcommand {

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

        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
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

            MessageHelper.success(receiver, lang.get("npc-command-copy-success", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-copy-cancelled", "npc", npc.getData().getName()));
        }

        return true;
    }
}
