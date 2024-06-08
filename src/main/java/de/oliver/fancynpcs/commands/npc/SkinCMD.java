package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.UUIDFetcher;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SkinCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    @Command("npc skin <npc> <skin>")
    @Permission("fancynpcs.command.npc.skin")
    public void onSkin(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "SkinCMD/skin") String skin
    ) {
        // Sending error message if NPC cannot have skin applied. Only players can have skins.
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("command_unsupported_npc_type").send(sender);
            return;
        }
        // Getting some information about input to handle command accordingly and improve message accuracy.
        final boolean isMirror = skin.equalsIgnoreCase("@mirror");
        final boolean isNone = skin.equalsIgnoreCase("@none");
        final boolean isURL = isURL(skin);
        if (isMirror) {
            // Calling event and updating the skin if not cancelled, sending error message otherwise.
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
            // Calling events and updating the skin if not cancelled, sending error message otherwise.
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
        } else if (isURL) {
            // Creating SkinFetcher from the specified texture URL.
            final SkinFetcher skinFetcher = new SkinFetcher(skin);
            // Sending error message if SkinFetcher has failed to load the skin.
            if (!skinFetcher.isLoaded()) {
                translator.translate("npc_skin_failure_invalid_url").replaceStripped("input", skin).send(sender);
                return;
            }
            // Calling events and updating the skin if not cancelled, sending error message otherwise.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkin(skinFetcher);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_url").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        // Handling as if user input is a player name. Because this may require sending a web request, we should (?) match against valid username pattern to make it somewhat injection-proof.
        } else if (USERNAME_PATTERN.matcher(skin).find()) {
            // Fetching UUID from the specified player name.
            final @Nullable UUID uuid = UUIDFetcher.getUUID(skin);
            // Sending error message if message if UUID fetch has (for whatever reason) failed. This can happen eg. when being rate limited.
            if (uuid == null) {
                translator.translate("npc_skin_failure_invalid_name_or_rate_limit").replaceStripped("input", skin).send(sender);
                return;
            }
            // Creating SkinFetcher from the fetched UUID.
            final SkinFetcher skinFetcher = new SkinFetcher(uuid.toString());
            // Sending error message if SkinFetcher has failed to load the skin.
            if (!skinFetcher.isLoaded()) {
                translator.translate("npc_skin_failure_invalid_name_or_rate_limit").replaceStripped("input", skin).send(sender);
                return;
            }
            // Calling events and updating the skin if not cancelled, sending error message otherwise.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkin(skinFetcher);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_name").replace("npc", npc.getData().getName()).replace("name", skin).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else {
            translator.translate("npc_skin_failure_invalid_name_or_url").replaceStripped("input", skin).send(sender);
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Suggestions("SkinCMD/skin")
    public List<String> suggestSkin(final CommandContext<CommandSender> context, final CommandInput input) {
        return new ArrayList<>() {{
            add("@none");
            add("@mirror");
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
        }};
    }

    /* UTILITY METHODS */

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

}
