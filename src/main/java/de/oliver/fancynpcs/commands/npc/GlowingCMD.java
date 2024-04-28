package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GlowingCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc glowing")
    @Permission("fancynpcs.command.npc.glowing")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_glowing_syntax").send(sender);
    }

    @Command("npc glowing <npc> [color]")
    @Permission("fancynpcs.command.npc.glowing")
    public void onCommand(final CommandSender sender, final Npc npc, final @Nullable GlowingColor color) {
        // Handling 'toggle' state.
        if (color == null) {
            // Inverting the current glowing state, so it works like a toggle.
            final boolean isGlowingToggled = !npc.getData().isGlowing();
            // Calling the event and updating the state if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, isGlowingToggled, sender).callEvent()) {
                npc.getData().setGlowing(isGlowingToggled);
                npc.updateForAll();
                translator.translate(isGlowingToggled ? "npc_glowing_set_true" : "npc_glowing_set_false").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        }
        // Handling 'disabled' state.
        else if (color == GlowingColor.DISABLED) {
            // Calling the event and updating the state if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, false, sender).callEvent()) {
                npc.getData().setGlowing(false);
                npc.updateForAll();
                translator.translate("npc_glowing_set_false").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        // Handling 'color' state, which means enabling glowing and changing the color to desired one.
        } else if (npc.getData().isGlowing() || new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, true, sender).callEvent()) {
            // Updating the glowing state, if previously disabled.
            if (!npc.getData().isGlowing()) {
                npc.getData().setGlowing(true);
                npc.updateForAll();
            }
            // Calling the event and updating the glowing color if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color.getColor(), sender).callEvent()) {
                npc.getData().setGlowingColor(color.getColor());
                npc.updateForAll();
                translator.translate("npc_glowing_set_color_success")
                        .replace("npc", npc.getData().getName())
                        .replace("color", ((SimpleMessage) translator.translate(color.translationKey)).getMessage())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    // NOTE: Perhaps this could be moved somewhere or be a standalone class.
    public enum GlowingColor {
        DISABLED(null, ""),
        BLACK(NamedTextColor.BLACK, "color_black"),
        DARK_BLUE(NamedTextColor.DARK_BLUE, "color_dark_blue"),
        DARK_GREEN(NamedTextColor.DARK_GREEN, "color_dark_green"),
        DARK_AQUA(NamedTextColor.DARK_AQUA, "color_dark_aqua"),
        DARK_RED(NamedTextColor.DARK_RED, "color_dark_red"),
        DARK_PURPLE(NamedTextColor.DARK_PURPLE, "color_dark_purple"),
        GOLD(NamedTextColor.GOLD, "color_gold"),
        GRAY(NamedTextColor.GRAY, "color_gray"),
        DARK_GRAY(NamedTextColor.DARK_GRAY, "color_dark_gray"),
        BLUE(NamedTextColor.BLUE, "color_blue"),
        GREEN(NamedTextColor.GREEN, "color_green"),
        AQUA(NamedTextColor.AQUA, "color_aqua"),
        RED(NamedTextColor.RED, "color_red"),
        LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE, "color_light_purple"),
        YELLOW(NamedTextColor.YELLOW, "color_yellow"),
        WHITE(NamedTextColor.WHITE, "color_white");

        // Handled as 'disabled' if set to null.
        private final @Nullable NamedTextColor color;

        private final @NotNull String translationKey;

        private GlowingColor(final @Nullable NamedTextColor color, final @NotNull String translationKey) {
            this.color = color;
            this.translationKey = translationKey;
        }

        public @Nullable NamedTextColor getColor() {
            return color;
        }

    }

}
