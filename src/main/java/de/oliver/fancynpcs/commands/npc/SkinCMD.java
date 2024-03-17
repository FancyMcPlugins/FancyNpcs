package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SkinCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length != 3 && args.length != 2) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        String skinName = args.length == 3 ? args[2] : receiver instanceof Player player ? player.getName() : "Steve";


        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        if (npc.getData().getType() != EntityType.PLAYER) {
            MessageHelper.error(receiver, lang.get("npc-must-be-player"));
            return false;
        }

        if (SkinFetcher.SkinType.getType(skinName) == SkinFetcher.SkinType.UUID) {
            UUID uuid = UUIDFetcher.getUUID(skinName);
            if (uuid == null) {
                MessageHelper.error(receiver, lang.get("npc-command-skin-invalid"));
                return false;
            }
            skinName = uuid.toString();
        }

        SkinFetcher skinFetcher = new SkinFetcher(skinName);
        if (!skinFetcher.isLoaded()) {
            MessageHelper.error(receiver, lang.get("npc-command-message-failed_header"));
            MessageHelper.error(receiver, lang.get("npc-command-skin-failed_url"));
            MessageHelper.error(receiver, lang.get("npc-command-skin-failed_limited"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setSkin(skinFetcher);
            npc.getData().setMirrorSkin(false);
            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            MessageHelper.success(receiver, lang.get("npc-command-skin-updated"));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
