package de.oliver.fancynpcs.commands;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.commands.arguments.NpcArgument;
import de.oliver.fancynpcs.commands.arguments.NpcAttributeArgument;
import de.oliver.fancynpcs.commands.exceptions.ReplyingParseException;
import de.oliver.fancynpcs.commands.npc.AttributeCMD;
import de.oliver.fancynpcs.commands.npc.CollidableCMD;
import de.oliver.fancynpcs.commands.npc.CreateCMD;
import de.oliver.fancynpcs.commands.npc.DisplayNameCMD;
import de.oliver.fancynpcs.commands.npc.EquipmentCMD;
import de.oliver.fancynpcs.commands.npc.FixCMD;
import de.oliver.fancynpcs.commands.npc.GlowingCMD;
import de.oliver.fancynpcs.commands.npc.InfoCMD;
import de.oliver.fancynpcs.commands.npc.InteractionCooldownCMD;
import de.oliver.fancynpcs.commands.npc.ListCMD;
import de.oliver.fancynpcs.commands.npc.MoveHereCMD;
import de.oliver.fancynpcs.commands.npc.MoveToCMD;
import de.oliver.fancynpcs.commands.npc.NearbyCMD;
import de.oliver.fancynpcs.commands.npc.NpcCMD;
import de.oliver.fancynpcs.commands.npc.RemoveCMD;
import de.oliver.fancynpcs.commands.npc.ShowInTabCMD;
import de.oliver.fancynpcs.commands.npc.SkinCMD;
import de.oliver.fancynpcs.commands.npc.TeleportCMD;
import de.oliver.fancynpcs.commands.npc.TurnToPlayerCMD;
import de.oliver.fancynpcs.commands.npc.TypeCMD;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import org.jetbrains.annotations.NotNull;

import static org.incendo.cloud.exception.handling.ExceptionHandler.unwrappingHandler;

// DEV NOTES:
// - Location suggestions might behave a bit weird, writing custom provider doesn't work either.
//     I'm fairly sure Cloud developers are aware of that issue and they'll fix it soon.
// - For the time being, due to the reasons below, Brigadier integration should be OFF by default:
//    a) Argument pop-ups don't work properly, they don't appear most of the time.
//    b) Suggestions are supplied per-whitespace, and not per-argument.
//    c) Location suggestions might also behave a bit weird, similarly as with Brigadier disabled.
public final class CloudCommandManager {

    private final @NotNull FancyNpcs plugin;

    private final @NotNull PaperCommandManager<CommandSender> commandManager;
    private final @NotNull AnnotationParser<CommandSender> annotationParser;

    public CloudCommandManager(final @NotNull FancyNpcs plugin, final boolean isBrigadier) {
        this.plugin = plugin;
        // Creating instance of Cloud's PaperCommandManager, which is used for anything command-related.
        this.commandManager = PaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        // Registering Brigadier, if available.
        if (isBrigadier && commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        }
        // Creating instance of AnnotationParser, which is used for parsing and registering commands.
        this.annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);
        // Registering parsers and suggestion providers.
        annotationParser.parse(NpcArgument.INSTANCE);
        annotationParser.parse(NpcAttributeArgument.INSTANCE);
        // Registering exception handlers.
        commandManager.exceptionController().registerHandler(ArgumentParseException.class, unwrappingHandler(ReplyingParseException.class));
        commandManager.exceptionController().registerHandler(ReplyingParseException.class, context -> context.exception().runnable().run());
    }

    /**
     * Registers plugin commands to the {@link PaperCommandManager}.
     */
    public @NotNull CloudCommandManager registerCommands() {
        annotationParser.parse(AttributeCMD.INSTANCE);
        annotationParser.parse(CollidableCMD.INSTANCE);
        annotationParser.parse(CreateCMD.INSTANCE);
        annotationParser.parse(DisplayNameCMD.INSTANCE);
        annotationParser.parse(EquipmentCMD.INSTANCE);
        annotationParser.parse(FixCMD.INSTANCE);
        annotationParser.parse(GlowingCMD.INSTANCE);
        annotationParser.parse(InfoCMD.INSTANCE);
        annotationParser.parse(InteractionCooldownCMD.INSTANCE);
        annotationParser.parse(ListCMD.INSTANCE);
        // annotationParser.parse(MessageCMD.INSTANCE);
        // annotationParser.parse(ServerCommandCMD.INSTANCE);
        // annotationParser.parse(PlayerCommandCMD.INSTANCE);
        annotationParser.parse(MoveHereCMD.INSTANCE);
        annotationParser.parse(NearbyCMD.INSTANCE);
        annotationParser.parse(NpcCMD.INSTANCE);
        annotationParser.parse(TurnToPlayerCMD.INSTANCE);
        annotationParser.parse(ShowInTabCMD.INSTANCE);
        annotationParser.parse(TeleportCMD.INSTANCE);
        annotationParser.parse(TypeCMD.INSTANCE);
        annotationParser.parse(RemoveCMD.INSTANCE);
        annotationParser.parse(SkinCMD.INSTANCE);
        annotationParser.parse(MoveToCMD.INSTANCE);
        // Returning this instance of CloudCommandManager to keep "builder-like" flow.
        return this;
    }

    /**
     * Returns the internal {@link PaperCommandManager} associated with this instance of {@link CloudCommandManager}.
     */
    public @NotNull PaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    /**
     * Returns the internal {@link AnnotationParser} associated with this instance of {@link CloudCommandManager}.
     */
    public @NotNull AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }

}
