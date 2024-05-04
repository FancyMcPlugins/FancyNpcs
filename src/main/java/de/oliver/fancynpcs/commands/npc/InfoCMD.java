package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.util.GlowingColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;

import org.jetbrains.annotations.NotNull;

public enum InfoCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat SECONDS_FORMAT = new DecimalFormat("#,###.#");

    @Command("npc info")
    @Permission("fancynpcs.command.npc.info")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_info_syntax").send(sender);
    }

    @Command("npc info <npc>")
    @Permission("fancynpcs.command.npc.info")
    public void onCommand(final CommandSender sender, final Npc npc) {
        final Location loc = npc.getData().getLocation();
        // Getting the translated glowing state. This should never throw because all supported NamedTextColor objects has their mapping in GlowingColor enum.
        final String glowingStateTranslated = (!npc.getData().isGlowing() || npc.getData().getGlowingColor() != null)
                ? ((SimpleMessage) translator.translate(GlowingColor.fromAdventure(npc.getData().getGlowingColor()).getTranslationKey())).getMessage()
                : ((SimpleMessage) translator.translate("disabled")).getMessage();
        // Sending general info to the sender.
        translator.translate("npc_info_general")
                .replace("name", npc.getData().getName())
                .replace("id", npc.getData().getId())
                .replace("id_short", npc.getData().getId().substring(0, 13) + "...")
                .replace("creator", npc.getData().getCreator().toString())
                .replace("creator_short", npc.getData().getCreator().toString().substring(0, 13) + "...")
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
                .replace("interaction_cooldown", SECONDS_FORMAT.format(npc.getData().getInteractionCooldown()) + "s")
                .send(sender);

        if (npc.getData().getServerCommand() != null) {
            translator.translate("npc_info_serverCommands_header").send(sender);
            translator.translate("npc_info_serverCommands_entry").replace("command", npc.getData().getServerCommand()).send(sender);
            translator.translate("npc_info_serverCommands_footer").send(sender);
        }

        if (!npc.getData().getPlayerCommands().isEmpty()) {
            translator.translate("npc_info_playerCommands_header").send(sender);
            npc.getData().getPlayerCommands().forEach(command -> {
                translator.translate("npc_info_playerCommands_entry").replace("command", command).send(sender);
            });
            translator.translate("npc_info_playerCommands_footer").send(sender);
        }

        if (!npc.getData().getMessages().isEmpty()) {
            translator.translate("npc_info_messages_header").send(sender);
            npc.getData().getMessages().forEach(message ->
                translator.translate("npc_info_messages_entry").replace("message", message).send(sender)
            );
            translator.translate("npc_info_messages_footer").send(sender);
        }

        if (!npc.getData().getEquipment().isEmpty()) {
            translator.translate("npc_info_equipment_header").send(sender);
            npc.getData().getEquipment().forEach((slot, item) -> {
                if (item.getType() == Material.AIR)
                    return;
                translator.translate("npc_info_equipment_entry")
                        .replace("slot", getTranslatedSlot(slot))
                        .addTagResolver(Placeholder.component("item", item.displayName().hoverEvent(item.asHoverEvent())))
                        .send(sender);
            });
            translator.translate("npc_info_equipment_footer").send(sender);
        }

        if (!npc.getData().getAttributes().isEmpty()) {
            translator.translate("npc_info_attributes_header").send(sender);
            npc.getData().getAttributes().forEach((attribute, value) ->
                    translator.translate("npc_info_attributes_entry").replace("attribute", attribute.getName()).replace("value", value).send(sender)
            );
            translator.translate("npc_info_attributes_footer").send(sender);
        }
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedBoolean(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("true")).getMessage() : ((SimpleMessage) translator.translate("false")).getMessage();
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedSlot(final @NotNull NpcEquipmentSlot slot) {
        return ((SimpleMessage) translator.translate(
                switch (slot) {
                    case MAINHAND -> "main_hand";
                    case OFFHAND -> "off_hand";
                    case HEAD -> "head";
                    case CHEST -> "chest";
                    case LEGS -> "legs";
                    case FEET -> "feet";
                }
        )).getMessage();
    }

}
