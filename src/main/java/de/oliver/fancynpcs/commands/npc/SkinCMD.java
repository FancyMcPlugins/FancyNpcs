package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SkinCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length != 3 && args.length != 2) {
            MessageHelper.error(player, lang.get("npc_commands-wrong_usage"));
            return false;
        }

        String skinName = args.length == 3 ? args[2] : player.getName();


        if (npc == null) {
            MessageHelper.error(player, lang.get("npc_commands-not_found"));
            return false;
        }

        if (npc.getData().getType() != EntityType.PLAYER) {
            MessageHelper.error(player, lang.get("npc_commands-must_player"));
            return false;
        }

        if (SkinFetcher.SkinType.getType(skinName) == SkinFetcher.SkinType.UUID) {
            UUID uuid = UUIDFetcher.getUUID(skinName);
            if (uuid == null) {
                MessageHelper.error(player, lang.get("npc_commands-skin-invalid"));
                return false;
            }
            skinName = uuid.toString();
        }

        SkinFetcher skinFetcher = new SkinFetcher(skinName);
        if (!skinFetcher.isLoaded()) {
            MessageHelper.error(player, lang.get("npc_commands-message-failed_header"));
            MessageHelper.error(player, lang.get("npc_commands-skin-failed_url"));
            MessageHelper.error(player, lang.get("npc_commands-skin-failed_limited"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, player);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setSkin(skinFetcher);
            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            MessageHelper.success(player, lang.get("npc_commands-skin-updated"));
        } else {
            MessageHelper.error(player, lang.get("npc_commands-skin-failed"));
        }

        return true;
    }
}
