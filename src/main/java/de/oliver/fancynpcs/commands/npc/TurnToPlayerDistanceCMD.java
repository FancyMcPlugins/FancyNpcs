package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TurnToPlayerDistanceCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc turn_to_player_distance [distance]")
    @Permission("fancynpcs.command.npc.turn_to_player_distance")
    public void onTurnToPlayerDistance(
            final @NotNull CommandSender sender,
            final @Nullable @Argument(suggestions = "turn_to_player_distance") Integer distance
    ) {
        if (distance == null) {
            // If no distance provided, show the current setting
            int currentDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();
            translator.translate("turn_to_player_distance_current")
                    .replace("distance", String.valueOf(currentDistance))
                    .send(sender);
            return;
        }

        // Validate the input
        if (distance <= 0) {
            translator.translate("turn_to_player_distance_invalid").send(sender);
            return;
        }

        // Update the config
        boolean success = FancyNpcs.getInstance().getFancyNpcConfig().setTurnToPlayerDistance(distance);
        
        if (success) {
            translator.translate("turn_to_player_distance_updated")
                    .replace("distance", String.valueOf(distance))
                    .send(sender);
        } else {
            translator.translate("turn_to_player_distance_failed").send(sender);
        }
    }
} 