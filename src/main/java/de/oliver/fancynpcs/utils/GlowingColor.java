package de.oliver.fancynpcs.utils;

import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Used 'info' and 'glowing' sub-commands.
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

    private final @Nullable NamedTextColor color;
    private final @NotNull String translationKey;

    GlowingColor(final @Nullable NamedTextColor color, final @NotNull String translationKey) {
        this.color = color;
        this.translationKey = translationKey;
    }

    public @Nullable NamedTextColor getColor() {
        return color;
    }

    public @NotNull String getTranslationKey() {
        return translationKey;
    }

    public static @NotNull GlowingColor fromAdventure(final @NotNull NamedTextColor color) {
        for (final GlowingColor glowingColor : GlowingColor.values())
            if (glowingColor.color != null && glowingColor.color.value() == color.value())
                return glowingColor;
        // Throwing exception if specified color is not mapped.
        throw new IllegalArgumentException("UNSUPPORTED COLOR");
    }

}
