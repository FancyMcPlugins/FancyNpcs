package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public enum SkinCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    /**
     * Returns {@code true} if provided string can be parsed to an {@link URL} object.
     */
    private static boolean isURL(final @NotNull String url) {
        try {
            new URL(url);
            return true;
        } catch (final MalformedURLException e) {
            return false;
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Command("npc skin <npc> <skin>")
    @Permission("fancynpcs.command.npc.skin")
    public void onSkin(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "SkinCMD/skin") String skin
    ) {
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("command_unsupported_npc_type").send(sender);
            return;
        }

        final boolean isMirror = skin.equalsIgnoreCase("@mirror");
        final boolean isNone = skin.equalsIgnoreCase("@none");
        if (isMirror) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MIRROR_SKIN, true, sender).callEvent()) {
                npc.getData().setMirrorSkin(true);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_mirror").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else if (isNone) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, null, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkin(null);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_none").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else {
            SkinFetcher.SkinData skinData;
            try {
                skinData = new SkinFetcher.SkinData(skin, null, null);
            } catch (Exception e) {
                translator.translate("npc_skin_failure_invalid").replaceStripped("input", skin).send(sender);
                return;
            }

            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinData, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkin(skinData);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set")
                        .replace("npc", npc.getData().getName())
                        .replace("name", skinData.identifier())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        }
    }

    /* UTILITY METHODS */

    @Suggestions("SkinCMD/skin")
    public List<String> suggestSkin(final CommandContext<CommandSender> context, final CommandInput input) {
        return new ArrayList<>() {{
            add("@none");
            add("@mirror");
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
        }};
    }

}
