package de.oliver.fancynpcs.commands.arguments;

import org.bukkit.FluidCollisionMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public enum LocationArgument {
    INSTANCE; // SINGLETON

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    @Suggestions("relative_location")
    public List<String> suggestLocation(final CommandContext<CommandSender> context, final CommandInput input) {
        if (context.sender() instanceof Player player) {
            final @Nullable RayTraceResult raytrace = player.rayTraceBlocks(32.0, FluidCollisionMode.ALWAYS);
            if (raytrace != null)
                return List.of(
                        COORDS_FORMAT.format(raytrace.getHitPosition().getX()) + " " +
                        COORDS_FORMAT.format(raytrace.getHitPosition().getY()) + " " +
                        COORDS_FORMAT.format(raytrace.getHitPosition().getZ()),
                        "~ ~ ~"
                );
            return List.of("~ ~ ~");
        }
        return Collections.emptyList();
    }
}
