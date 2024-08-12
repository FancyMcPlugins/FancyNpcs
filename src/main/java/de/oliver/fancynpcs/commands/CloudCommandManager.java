package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.Message;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.commands.arguments.LocationArgument;
import de.oliver.fancynpcs.commands.arguments.NpcArgument;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import de.oliver.fancynpcs.commands.npc.*;
import de.oliver.fancynpcs.utils.GlowingColor;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.exception.handling.ExceptionHandlerRegistration;
import org.incendo.cloud.exception.parsing.NumberParseException;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.incendo.cloud.exception.handling.ExceptionHandler.unwrappingHandler;

// DEV NOTES:
// - For the time being, due to the reasons below, Brigadier integration should be OFF by default:
//    a) Argument pop-ups don't work properly, they don't appear most of the time.
//    b) Suggestions are supplied per-whitespace, and not per-argument.
public final class CloudCommandManager {

    private final @NotNull FancyNpcs plugin;

    private final @NotNull LegacyPaperCommandManager<CommandSender> commandManager;
    private final @NotNull AnnotationParser<CommandSender> annotationParser;

    public CloudCommandManager(final @NotNull FancyNpcs plugin, final boolean isBrigadier) {
        this.plugin = plugin;
        // Creating instance of Cloud's LegacyPaperCommandManager, which is used for anything command-related.
        this.commandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        // Registering Brigadier, if available.
        if (isBrigadier && commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER))
            commandManager.registerBrigadier();
        // Creating instance of AnnotationParser, which is used for parsing and registering commands.
        this.annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);
    }

    /**
     * Registers arguments (parsers and suggestion providers) to the {@link LegacyPaperCommandManager}.
     */
    public @NotNull CloudCommandManager registerArguments() {
        annotationParser.parse(NpcArgument.INSTANCE);
        annotationParser.parse(LocationArgument.INSTANCE);
        // Returning this instance of CloudCommandManager to keep "builder-like" flow.
        return this;
    }

    /**
     * Registers exception handlers to the {@link LegacyPaperCommandManager}.
     */
    public @NotNull CloudCommandManager registerExceptionHandlers() {
        final Translator translator = plugin.getTranslator();
        // Unwrapping some causes of ArgumentParseException to be handled in standalone exception handlers.
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(NumberParseException.class));
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(BooleanParser.BooleanParseException.class));
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(EnumParser.EnumParseException.class));
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(WorldParser.WorldParseException.class));
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(ReplyingParseException.class));
        // Overriding some default handlers to send specialized messages.
        commandManager.exceptionController().registerHandler(NoPermissionException.class, (exceptionContext) -> {
            translator.translate("command_missing_permissions").send(exceptionContext.context().sender());
        });
        // DEV NOTE: No need to compare sender types until we decide to make a console-only command. Should get the job done for the time being.
        commandManager.exceptionController().registerHandler(InvalidCommandSenderException.class, (exceptionContext) -> {
            translator.translate("command_player_only").send(exceptionContext.context().sender());
        });
        commandManager.exceptionController().registerHandler(NumberParseException.class, (exceptionContext) -> {
            translator.translate("command_invalid_number")
                    .replaceStripped("input", exceptionContext.exception().input())
                    .replace("min", exceptionContext.exception().range().min().toString())
                    .replace("max", exceptionContext.exception().range().max().toString())
                    .send(exceptionContext.context().sender());
        });
        commandManager.exceptionController().registerHandler(BooleanParser.BooleanParseException.class, (exceptionContext) -> {
            translator.translate("command_invalid_boolean")
                    .replaceStripped("input", exceptionContext.exception().input())
                    .send(exceptionContext.context().sender());
        });
        commandManager.exceptionController().registerHandler(WorldParser.WorldParseException.class, (exceptionContext) -> {
            translator.translate("command_invalid_world")
                    .replaceStripped("input", exceptionContext.exception().input())
                    .send(exceptionContext.context().sender());
        });
        // DEV NOTE: Temporary solution until https://github.com/Incendo/cloud-minecraft/pull/70 is merged.
        commandManager.exceptionController().register(ExceptionHandlerRegistration.<CommandSender, ArgumentParseException>builder(TypeToken.get(ArgumentParseException.class))
                .exceptionFilter(exception -> exception.getCause() instanceof ParserException parserException && parserException.argumentParserClass() == LocationParser.class)
                .exceptionHandler(exceptionContext -> {
                    final ParserException exception = (ParserException) exceptionContext.exception().getCause();
                    final String input = exception.captionVariables()[0].value(); // Should never throw.
                    translator.translate("command_invalid_location")
                            .replaceStripped("input", !input.isBlank() ? input : "N/A") // Under certain conditions, input is not passed to the exception.
                            .send(exceptionContext.context().sender());
                }).build()
        );
        commandManager.exceptionController().registerHandler(EnumParser.EnumParseException.class, (exceptionContext) -> {
            String translationKey = "command_invalid_enum_generic";
            // Comparing exception enum class and choosing specialized messages.
            if (exceptionContext.exception().enumClass() == ListCMD.SortType.class)
                translationKey = "command_invalid_list_sort_type";
            else if (exceptionContext.exception().enumClass() == NearbyCMD.SortType.class)
                translationKey = "command_invalid_nearby_sort_type";
            else if (exceptionContext.exception().enumClass() == EntityType.class)
                translationKey = "command_invalid_entity_type";
            else if (exceptionContext.exception().enumClass() == GlowingColor.class)
                translationKey = "command_invalid_glowing_color";
            // Sending error message to the sender. In case no specialized message has been found, a generic one is used instead.
            translator.translate(translationKey)
                    .replaceStripped("input", exceptionContext.exception().input())
                    .replace("enum", exceptionContext.exception().enumClass().getSimpleName().toLowerCase())
                    .send(exceptionContext.context().sender());
        });
        // ReplyingParseException is thrown from custom argument types and is handled there.
        commandManager.exceptionController().registerHandler(ReplyingParseException.class, context -> context.exception().runnable().run());
        // InvalidSyntaxException is thrown when user specified syntax don't match any command.
        commandManager.exceptionController().registerHandler(InvalidSyntaxException.class, (exceptionContext) -> {
            // Creating a StringBuilder which is then appended with (known/existing) command literals.
            final StringBuilder translationKeyBuilder = new StringBuilder("command_syntax.");
            // Iterating over current command chain and appending literals, as described above.
            exceptionContext.exception().currentChain().stream()
                    .filter(c -> c.type() == CommandComponent.ComponentType.LITERAL)
                    .forEach(literal -> translationKeyBuilder.append(literal.name()).append(' '));
            // Trimming input (last character ends up being blank) and replacing whitespaces with underscores, as that's how translations are defined inside the language file.
            final String translationKey = translationKeyBuilder.toString().trim().replace(' ', '_');
            // Getting the message, it's not finished as there we need to handle fallback language etc.
            final @Nullable Message message = Optional.ofNullable(plugin.getTranslator().getSelectedLanguage().getMessage(translationKey))
                    .orElse(plugin.getTranslator().getFallbackLanguage().getMessage(translationKey));
            // "Fall-backing" to generic syntax error, if no specialized syntax message has been defined in the language file.
            if (message == null) {
                plugin.getTranslator().translate("command_invalid_syntax_generic")
                        .replace("syntax", exceptionContext.exception().correctSyntax())
                        .send(exceptionContext.context().sender());
                return;
            }
            message.send(exceptionContext.context().sender());
        });
        // Returning this instance of CloudCommandManager to keep "builder-like" flow.
        return this;
    }

    /**
     * Registers plugin commands to the {@link LegacyPaperCommandManager}.
     */
    public @NotNull CloudCommandManager registerCommands() {
        annotationParser.parse(AttributeCMD.INSTANCE);
        annotationParser.parse(CollidableCMD.INSTANCE);
        annotationParser.parse(CopyCMD.INSTANCE);
        annotationParser.parse(CreateCMD.INSTANCE);
        annotationParser.parse(DisplayNameCMD.INSTANCE);
        annotationParser.parse(EquipmentCMD.INSTANCE);
        annotationParser.parse(FancyNpcsCMD.INSTANCE);
        annotationParser.parse(FixCMD.INSTANCE);
        annotationParser.parse(GlowingCMD.INSTANCE);
        annotationParser.parse(InfoCMD.INSTANCE);
        annotationParser.parse(InteractionCooldownCMD.INSTANCE);
        annotationParser.parse(ListCMD.INSTANCE);
        annotationParser.parse(MessageCMD.INSTANCE);
        annotationParser.parse(MoveHereCMD.INSTANCE);
        annotationParser.parse(MoveToCMD.INSTANCE);
        annotationParser.parse(NearbyCMD.INSTANCE);
        annotationParser.parse(HelpCMD.INSTANCE);
        annotationParser.parse(PlayerCommandCMD.INSTANCE);
        annotationParser.parse(RemoveCMD.INSTANCE);
        annotationParser.parse(ServerCommandCMD.INSTANCE);
        annotationParser.parse(ShowInTabCMD.INSTANCE);
        annotationParser.parse(SkinCMD.INSTANCE);
        annotationParser.parse(TeleportCMD.INSTANCE);
        annotationParser.parse(TurnToPlayerCMD.INSTANCE);
        annotationParser.parse(TypeCMD.INSTANCE);


        String mcVersion = Bukkit.getMinecraftVersion();
        if (mcVersion.equals("1.20.5") || mcVersion.equals("1.20.6") || mcVersion.equals("1.21") || mcVersion.equals("1.21.1")) {
            annotationParser.parse(ScaleCMD.INSTANCE);
        }

        return this;
    }

    /**
     * Returns the internal {@link LegacyPaperCommandManager} associated with this instance of {@link CloudCommandManager}.
     */
    public @NotNull LegacyPaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    /**
     * Returns the internal {@link AnnotationParser} associated with this instance of {@link CloudCommandManager}.
     */
    public @NotNull AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }

}
