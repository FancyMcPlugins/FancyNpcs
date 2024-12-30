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

import java.util.List;

import org.jetbrains.annotations.NotNull;

public enum VisibilityDistanceCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private final List<String> DISTANCE_SUGGESTIONS = List.of("always_visible", "default", "not_visible");

    @Command("npc visibility_distance <npc> <distance>")
    @Permission("fancynpcs.command.npc.visibility_distance")
    public void onVisibilityDistance(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Argument(parserName = "VisibilityDistanceCMD/distance") int distance
    ) {
        final int finalDistance = Math.clamp(distance, -1, Integer.MAX_VALUE);
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.VISIBILITY_DISTANCE, distance, sender).callEvent()) {
            npc.getData().setVisibilityDistance(finalDistance);
            npc.updateForAll();
            translator.translate(finalDistance == -1 ? "npc_visibility_distance_set_default" : finalDistance == 0 ? "npc_visibility_distance_set_not_visible" : finalDistance == Integer.MAX_VALUE ? "npc_visibility_distance_set_always_visible" : "npc_visibility_distance_set_value")
                    .replace("npc", npc.getData().getName())
                    .replace("distance", (finalDistance > -1) ? String.valueOf(finalDistance) : String.valueOf(FancyNpcs.getInstance().getFancyNpcConfig().getVisibilityDistance()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Parser(name = "VisibilityDistanceCMD/distance", suggestions = "VisibilityDistanceCMD/distance")
    public @NotNull Integer parse(final CommandContext<CommandSender> context, final CommandInput input) {
        // If 'default' string is provided, it is being handled as -1.
        if (input.peekString().equalsIgnoreCase("default")) {
            input.readString();
            return -1;
        }
        // If 'not_visible' string is provided, it is being handled as 0.
        if (input.peekString().equalsIgnoreCase("not_visible")) {
            input.readString();
            return 0;
        }
        // If 'always_visible' string is provided, it is being handled as Integer.MAX_VALUE.
        if (input.peekString().equalsIgnoreCase("always_visible")) {
            input.readString();
            return Integer.MAX_VALUE;
        }
        // Otherwise, reading next argument as int.
        return input.readInteger();
    }

    @Suggestions("VisibilityDistanceCMD/distance")
    public @NotNull List<String> suggest(final CommandContext<CommandSender> context, final CommandInput input) {
        return DISTANCE_SUGGESTIONS;
    }

}
