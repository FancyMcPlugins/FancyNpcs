package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.Interval.Unit;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum InteractionCooldownCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?<=\\d)(?=\\D)");

    @Command("npc interaction_cooldown <npc> <cooldown>")
    @Permission("fancynpcs.command.npc.interaction_cooldown")
    public void onInteractionCooldown(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(parserName = "InteractionCooldownCMD/cooldown") Interval cooldown
    ) {
        // Calling the event and updating the cooldown if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.INTERACTION_COOLDOWN, cooldown, sender).callEvent()) {
            npc.getData().setInteractionCooldown((float) cooldown.as(Unit.MILLISECONDS) / 1000F);
            translator.translate(cooldown.as(Unit.MILLISECONDS) != 0 ? "npc_interaction_cooldown_set" : "npc_interaction_cooldown_disabled")
                    .replace("npc", npc.getData().getName())
                    .replace("time", cooldown.toString())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Parser(name = "InteractionCooldownCMD/cooldown", suggestions = "InteractionCooldownCMD/cooldown")
    public @NotNull Interval parse(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString();
        // Handling 'disabled' as interval of 0 milliseconds. This is how plugin determines whether interaction cooldown is enabled or not.
        if (value.equalsIgnoreCase("disabled"))
            return Interval.of(0, Unit.MILLISECONDS);
        // Splitting user input between a digit and a letter.
        final String[] split = SPLIT_PATTERN.split(value);
        final @Nullable Long num = (split.length == 2) ? parseLong(split[0]) : null;
        final @Nullable Unit unit = (split.length == 2) ? Unit.fromShortCode(split[1].toLowerCase()) : null;
        // Sending error message to the sender if input cannot be converted to a valid interval.
        if (num == null || unit == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_interval").replaceStripped("input", value).send(context.sender()));
        return Interval.of(Math.max(0, num), unit);
    }

    @Suggestions(value = "InteractionCooldownCMD/cooldown")
    public @NotNull Collection<String> suggest(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString();
        // Splitting user input between a digit and a letter.
        final String[] split = SPLIT_PATTERN.split(value);
        final @Nullable Long num = parseLong(split[0]);
        // Checking that the number is not null.
        return (num == null || num <= 0)
                ? List.of("30s", "5min", "8h", "disabled")
                : new ArrayList<>() {{
                    add("disabled");
                    addAll(Stream.of(
                            Pair.of(Interval.of(num, Unit.MILLISECONDS), Unit.MILLISECONDS),
                            Pair.of(Interval.of(num, Unit.SECONDS), Unit.SECONDS),
                            Pair.of(Interval.of(num, Unit.MINUTES), Unit.MINUTES),
                            Pair.of(Interval.of(num, Unit.HOURS), Unit.HOURS)
                    ).map(pair -> num + pair.second().getShortCode()).toList());
                }};
    }

    /* UTILITY METHODS */

    private @Nullable Long parseLong(final @NotNull String value) {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

}
