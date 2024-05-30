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
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum NpcArgument {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    // This parser does not specify a name, making it default parser for the returned type.
    @Parser(name = "", suggestions = "npc")
    public @NotNull Npc parse(final CommandContext<CommandSender> context, final CommandInput input) {
        // Reading next argument as single/literal String.
        final String value = input.readString();
        // Getting the NPC from the manager. This can be name or optionally (under certain circumstances) UUID of the NPC.
        final @Nullable Npc npc = !isUUID(value)
                // Not an UUID, getting NPC from name.
                ? FancyNpcs.getInstance().getNpcManager().getNpc(value)
                // Input is UUID, getting the NPC that way. If PLAYER NPCS FLAG is enabled, sender is required to have 'fancynpcs.admin' permission.
                : !FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() || context.sender().hasPermission("fancynpcs.admin")
                        ? FancyNpcs.getInstance().getNpcManager().getNpcById(value)
                        : null;
        // Throwing exception if no NPC with given name or UUID exist.
        if (npc == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_npc").replaceStripped("input", value).send(context.sender()));
        // Throwing exception if PLAYER NPCS FLAG is enabled and sender is not creator of the specified NPC.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player sender && !npc.getData().getCreator().equals(sender.getUniqueId()))
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_npc").replaceStripped("input", value).send(context.sender()));
        return npc;
    }

    @Suggestions("npc") // NOTE: Consider caching, might not be necessary but should be kept in mind.
    public List<String> suggestions(final CommandContext<C> context, final CommandInput input) {
        return (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player sender)
                // PLAYER NPCS FLAG is enabled and sender is player; Filtering NPCs and showing only those that are created by the sender.
                ? FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream()
                        .filter(npc -> npc.getData().getCreator().equals(sender.getUniqueId()))
                        .map(npc -> npc.getData().getName())
                        .toList()
                // PLAYER NPCS FLAG is disabled or sender is console; Showing all NPCs.
                : FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream().map(npc -> npc.getData().getName()).toList();
    }

    /**
     * Returns {@code true} if provided {@link String} can be converted to a valid {@link UUID}. Otherwise {@code false} is returned.
     * */
    private static boolean isUUID(final @NotNull String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

}
