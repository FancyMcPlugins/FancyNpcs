package de.oliver.fancynpcs.commands.arguments;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public enum NpcArgument {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Parser(suggestions = "npc")
    public @NotNull Npc parse(final CommandContext<CommandSender> context, final CommandInput input) {
        // Reading next argument as single/literal String.
        final String value = input.readString();
        // Getting the NPC from the manager.
        final Npc npc =  FancyNpcs.getInstance().getNpcManager().getNpc(value);
        // Throwing exception if NPC does not exist.
        if (npc == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_npc").replace("input", value).send(context.sender()));
        // Throwing exception if PLAYER NPCS FLAG is enabled and sender (player) is not creator of the specified NPC.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player sender && !npc.getData().getCreator().equals(sender.getUniqueId()))
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_npc").replace("input", value).send(context.sender()));
        // Returning...
        return npc;
    }

    @Suggestions("npc") // NOTE: Consider caching, might not be necessary but should be kept in mind.
    public List<String> suggestions(final CommandContext<C> context, final CommandInput input) {
        return (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player sender)
                // PLAYER NPCS FLAG is enabled and sender is player; Filtering NPCs and showing only those that are created by the sender.
                ? FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream()
                        .filter(npc -> npc.getData().getCreator().equals(sender.getUniqueId()))
                        .map(npc -> npc.getData().getName())
                        .sorted()
                        .toList()
                // PLAYER NPCS FLAG is disabled or sender is console; Showing all NPCs.
                : FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream().map(npc -> npc.getData().getName()).sorted().toList();
    }

}
