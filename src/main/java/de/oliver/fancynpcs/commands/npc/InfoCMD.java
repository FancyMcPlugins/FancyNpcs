package de.oliver.fancynpcs.commands.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.Interval.Unit;
import de.oliver.fancynpcs.utils.GlowingColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public enum InfoCMD {
    INSTANCE; // SINGLETON

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc info <npc>")
    @Permission("fancynpcs.command.npc.info")
    public void onInfo(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        final Location loc = npc.getData().getLocation();
        final Interval interactionCooldown = Interval.of(npc.getData().getInteractionCooldown(), Unit.SECONDS);
        // Getting the translated glowing state. This should never throw because all supported NamedTextColor objects has their mapping in GlowingColor enum.
        final String glowingStateTranslated = (npc.getData().isGlowing() && npc.getData().getGlowingColor() != null)
                ? ((SimpleMessage) translator.translate(GlowingColor.fromAdventure(npc.getData().getGlowingColor()).getTranslationKey())).getMessage()
                : ((SimpleMessage) translator.translate("disabled")).getMessage();
        // Getting the creator player profile, this will be completed from cache in order to get name of the player.
        final PlayerProfile creatorProfile = Bukkit.createProfile(npc.getData().getCreator());
        translator.translate("npc_info_general")
                .replace("name", npc.getData().getName())
                .replace("id", npc.getData().getId())
                .replace("id_short", npc.getData().getId().substring(0, 13) + "...")
                .replace("creator_uuid", npc.getData().getCreator().toString())
                .replace("creator_uuid_short", creatorProfile.completeFromCache() ? npc.getData().getCreator().toString().substring(0, 13) + "..." : npc.getData().getCreator().toString().substring(0, 13))
                .replace("creator_name", creatorProfile.getName() != null ? creatorProfile.getName() : ((SimpleMessage) translator.translate("unknown")).getMessage())
                .replace("displayname", npc.getData().getDisplayName())
                .replace("type", "<lang:" + npc.getData().getType().translationKey() + ">") // Not ideal solution but should work fine for now.
                .replace("location_x", COORDS_FORMAT.format(loc.x()))
                .replace("location_y", COORDS_FORMAT.format(loc.y()))
                .replace("location_z", COORDS_FORMAT.format(loc.z()))
                .replace("world", loc.getWorld().getName())
                .replace("glow", glowingStateTranslated)
                .replace("is_collidable", getTranslatedBoolean(npc.getData().isCollidable()))
                .replace("is_turn_to_player", getTranslatedBoolean(npc.getData().isTurnToPlayer()))
                .replace("is_show_in_tab", getTranslatedBoolean(npc.getData().isShowInTab()))
                .replace("is_skin_mirror", getTranslatedBoolean(npc.getData().isMirrorSkin()))
                .replace("interaction_cooldown", npc.getData().getInteractionCooldown() <= 0 ? getTranslatedState(false) : interactionCooldown.toString())
                .replace("scale", String.valueOf(npc.getData().getScale()))
                .replace("messages_total", String.valueOf(npc.getData().getMessages().size()))
                .replace("player_commands_total", String.valueOf(npc.getData().getPlayerCommands().size()))
                .replace("server_commands_total", String.valueOf(npc.getData().getServerCommands().size()))
                .send(sender);
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedBoolean(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("true")).getMessage() : ((SimpleMessage) translator.translate("false")).getMessage();
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedState(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("enabled")).getMessage() : ((SimpleMessage) translator.translate("disabled")).getMessage();
    }

}
