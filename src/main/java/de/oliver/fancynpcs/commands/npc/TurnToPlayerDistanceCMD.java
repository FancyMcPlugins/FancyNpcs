package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum TurnToPlayerDistanceCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private final List<String> DISTANCE_SUGGESTIONS = List.of("default");

    @Command("npc turn_to_player_distance <npc> <distance>")
    @Permission("fancynpcs.command.npc.turn_to_player_distance")
    public void onTurnToPlayerDistance(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(parserName = "TurnToPlayerDistanceCMD/distance") int distance
    ) {
        if (distance < -1) {
            translator.translate("npc_turn_to_player_distance_invalid").send(sender);
            return;
        }
        
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER_DISTANCE, distance, sender).callEvent()) {
            npc.getData().setTurnToPlayerDistance(distance);
            
            if (distance == -1) {
                // Using default distance
                int defaultDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();
                translator.translate("npc_turn_to_player_distance_default")
                        .replace("npc", npc.getData().getName())
                        .replace("distance", String.valueOf(defaultDistance))
                        .send(sender);
            } else {
                // Using custom distance
                translator.translate("npc_turn_to_player_distance_set")
                        .replace("npc", npc.getData().getName())
                        .replace("distance", String.valueOf(distance))
                        .send(sender);
            }
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Parser(name = "TurnToPlayerDistanceCMD/distance", suggestions = "TurnToPlayerDistanceCMD/distance")
    public @NotNull Integer parse(final CommandContext<CommandSender> context, final CommandInput input) {
        // If 'default' string is provided, it is being handled as -1.
        if (input.peekString().equalsIgnoreCase("default")) {
            input.readString();
            return -1;
        }
        // Otherwise, reading next argument as int.
        return input.readInteger();
    }

    @Suggestions("TurnToPlayerDistanceCMD/distance")
    public @NotNull List<String> suggest(final CommandContext<CommandSender> context, final CommandInput input) {
        return DISTANCE_SUGGESTIONS;
    }
} 