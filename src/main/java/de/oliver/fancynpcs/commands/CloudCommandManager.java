package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.message.Message;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.commands.arguments.LocationArgument;
import de.oliver.fancynpcs.commands.arguments.NpcArgument;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import de.oliver.fancynpcs.commands.npc.AttributeCMD;
import de.oliver.fancynpcs.commands.npc.CollidableCMD;
import de.oliver.fancynpcs.commands.npc.CopyCMD;
import de.oliver.fancynpcs.commands.npc.CreateCMD;
import de.oliver.fancynpcs.commands.npc.DisplayNameCMD;
import de.oliver.fancynpcs.commands.npc.EquipmentCMD;
import de.oliver.fancynpcs.commands.npc.FixCMD;
import de.oliver.fancynpcs.commands.npc.GlowingCMD;
import de.oliver.fancynpcs.commands.npc.InfoCMD;
import de.oliver.fancynpcs.commands.npc.InteractionCooldownCMD;
import de.oliver.fancynpcs.commands.npc.ListCMD;
import de.oliver.fancynpcs.commands.npc.MessageCMD;
import de.oliver.fancynpcs.commands.npc.MoveHereCMD;
import de.oliver.fancynpcs.commands.npc.MoveToCMD;
import de.oliver.fancynpcs.commands.npc.NearbyCMD;
import de.oliver.fancynpcs.commands.npc.NpcHelpCMD;
import de.oliver.fancynpcs.commands.npc.PlayerCommandCMD;
import de.oliver.fancynpcs.commands.npc.RemoveCMD;
import de.oliver.fancynpcs.commands.npc.ServerCommandCMD;
import de.oliver.fancynpcs.commands.npc.ShowInTabCMD;
import de.oliver.fancynpcs.commands.npc.SkinCMD;
import de.oliver.fancynpcs.commands.npc.TeleportCMD;
import de.oliver.fancynpcs.commands.npc.TurnToPlayerCMD;
import de.oliver.fancynpcs.commands.npc.TypeCMD;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        // Registering parsers and suggestion providers.
        annotationParser.parse(NpcArgument.INSTANCE);
        annotationParser.parse(LocationArgument.INSTANCE);
        // Registering exception handlers.
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(ReplyingParseException.class));
        commandManager.exceptionController().registerHandler(ReplyingParseException.class, context -> context.exception().runnable().run());
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
            // Currently, Translator#translate(String) throws NPE on invalid message and that's why we're not using it here.
            final @Nullable Message message = plugin.getTranslator().getSelectedLanguage().getMessage(translationKey);
            // "Fall-backing" to generic syntax error, if no specialized syntax message has been defined in the language file.
            if (message == null) {
                plugin.getTranslator().translate("command_invalid_syntax_generic")
                        .replace("syntax", exceptionContext.exception().correctSyntax())
                        .send(exceptionContext.context().sender());
                return;
            }
            message.send(exceptionContext.context().sender());
        });
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
        annotationParser.parse(NpcHelpCMD.INSTANCE);
        annotationParser.parse(PlayerCommandCMD.INSTANCE);
        annotationParser.parse(ServerCommandCMD.INSTANCE);
        annotationParser.parse(RemoveCMD.INSTANCE);
        annotationParser.parse(ShowInTabCMD.INSTANCE);
        annotationParser.parse(SkinCMD.INSTANCE);
        annotationParser.parse(TeleportCMD.INSTANCE);
        annotationParser.parse(TurnToPlayerCMD.INSTANCE);
        annotationParser.parse(TypeCMD.INSTANCE);
        // Returning this instance of CloudCommandManager to keep "builder-like" flow.
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
