package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.commands.Subcommand;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfoCMD implements Subcommand {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat SECONDS_FORMAT = new DecimalFormat("#,###.#");

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            translator.translate("command_invalid_npc").replace("npc", args[1]).send(sender);
            return false;
        }

        Location loc = npc.getData().getLocation();

        translator.translate("npc_info_general")
                .replace("name", npc.getData().getName())
                .replace("id", npc.getData().getId())
                .replace("id_short", npc.getData().getId().substring(0, 13) + "...")
                .replace("internal_id", "2")
                .replace("creator", npc.getData().getCreator().toString())
                .replace("creator_short", npc.getData().getCreator().toString().substring(0, 13) + "...")
                .replace("displayname", npc.getData().getDisplayName())
                .replace("type", "<lang:" + npc.getData().getType().translationKey() + ">") // Not ideal solution but should work fine for now.
                .replace("location_x", COORDS_FORMAT.format(loc.x()))
                .replace("location_y", COORDS_FORMAT.format(loc.y()))
                .replace("location_z", COORDS_FORMAT.format(loc.z()))
                .replace("world", loc.getWorld().getName())
                .replace("is_glowing", getTranslatedBoolean(npc.getData().isGlowing()))
                .replace("glowing_color", getFormattedColor(npc.getData().getGlowingColor()))
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
        return false;
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

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private static @NotNull String getFormattedColor(final @NotNull NamedTextColor color) {
        final int colorCode = color.value();
        return switch (colorCode) {
            case 0 -> "Black";
            case 170 -> "Dark Blue";
            case 43520 -> "Dark Green";
            case 43690 -> "Dark Aqua";
            case 11141120 -> "Dark Red";
            case 11141290 -> "Dark Purple";
            case 16755200 -> "Gold";
            case 11184810 -> "Gray";
            case 5592405 -> "Dark Gray";
            case 5592575 -> "Blue";
            case 5635925 -> "Green";
            case 5636095 -> "Aqua";
            case 16733525 -> "Red";
            case 16733695 -> "Light Purple";
            case 16777045 -> "Yellow";
            case 16777215 -> "White";
            default -> "Unknown";
        };
    }

}
