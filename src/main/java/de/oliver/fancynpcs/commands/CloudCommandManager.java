package de.oliver.fancynpcs.commands;

import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.parser.KeyedWorldParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

public final class CloudCommandManager {

    private final @NotNull FancyNpcs plugin;

    private final @NotNull PaperCommandManager<CommandSender> commandManager;
    private final @NotNull AnnotationParser<CommandSender> annotationParser;

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    // NOTE: We may provide configuration option for brigadier, hence why it's parameter here. Subject to change.
    public CloudCommandManager(final @NotNull FancyNpcs plugin, final boolean isBrigadier) {
        this.plugin = plugin;
        // Creating instance of Cloud's PaperCommandManager, which is used for anything command-related.
        this.commandManager = PaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        // Registering Brigadier, if available:
        if (isBrigadier && commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        }
        // Creating instance of AnnotationParser, which is used for parsing and registering commands.
        this.annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);
        // Registering parsers and suggestion providers.
        commandManager.parserRegistry().registerNamedParser("keyedWorld", KeyedWorldParser.keyedWorldParser());
        commandManager.parserRegistry().registerSuggestionProvider("keyedWorld", KEYED_WORLD_SUGGESTIONS);
        commandManager.parserRegistry().registerSuggestionProvider("contextAwareLocation", RELATIVE_COORDS_SUGGESTION);
    }

    /**
     * Registers plugin commands to the {@link PaperCommandManager}.
     */
    public @NotNull CloudCommandManager registerCommands() {
        // TO-DO

        return this;
    }

    // Copied from KeyedWorldParser because otherwise suggestions wouldn't work. (bug?)
    private static final SuggestionProvider<CommandSender> KEYED_WORLD_SUGGESTIONS = (context, input) -> {
        final List<World> worlds = Bukkit.getWorlds();
        final List<Suggestion> completions = new ArrayList<>(worlds.size() * 2);
        for (final World world : worlds) {
            final NamespacedKey key = world.getKey();
            if (input.hasRemainingInput() && key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
                completions.add(Suggestion.suggestion(key.getKey()));
            }
            completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
        }
        return CompletableFuture.completedFuture(completions);
    };

    private static final SuggestionProvider<CommandSender> RELATIVE_COORDS_SUGGESTION = (context, input) -> {
        if (context.sender() instanceof Player sender) {
            // TO-DO
        }
        // Console should not get any completions.
        return CompletableFuture.completedFuture(Collections.emptyList());
    };

}
