package de.oliver.fancynpcs.commands.arguments;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class NpcArgument {

    public static final NpcArgument INSTANCE = new NpcArgument();

    private final Translator translator;
    private final NpcManager npcManager;

    private NpcArgument() {
        this.translator = FancyNpcs.getInstance().getTranslator();
        this.npcManager = FancyNpcs.getInstance().getNpcManager();
    }

    /**
     * Returns {@code true} if provided {@link String} can be converted to a valid {@link UUID}. Otherwise {@code false} is returned.
     */
    private static boolean isUUID(final @NotNull String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Default parser for {@link Npc} argument.
     */
    @Parser(name = "", suggestions = "npc")
    public @NotNull Npc parse(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString();

        Npc npc;
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player playerSender) {
            npc = npcManager.getNpc(value, playerSender.getUniqueId());
        } else {

            if (isUUID(value)) {
                npc = npcManager.getNpcById(value);
            } else {
                npc = npcManager.getNpc(value);
            }
        }

        if (npc == null) {
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_npc").replaceStripped("input", value).send(context.sender()));
        }

        return npc;
    }

    /**
     * Suggestions provider for {@link Npc} argument.
     */
    @Suggestions("npc") // NOTE: Consider caching, might not be necessary but should be kept in mind.
    public List<String> suggestions(final CommandContext<C> context, final CommandInput input) {
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.sender() instanceof Player playerSender) {
            return npcManager.getAllNpcs().stream()
                    .filter(npc -> npc.getData().getCreator().equals(playerSender.getUniqueId()))
                    .map(npc -> npc.getData().getName())
                    .toList();
        } else {
            return npcManager.getAllNpcs().stream()
                    .map(npc -> npc.getData().getName())
                    .toList();
        }
    }
}
