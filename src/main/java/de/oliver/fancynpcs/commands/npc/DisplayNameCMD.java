package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;

public enum DisplayNameCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final List<String> SUGGESTIONS = List.of("<empty>");

    @Suggestions("empty")
    public List<String> suggestions(final CommandContext<CommandSender> sender, CommandInput input) {
        return SUGGESTIONS;
    }

    @Command("npc displayname")
    @Permission("fancynpcs.command.npc.displayname")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_displayname_syntax").send(sender);
    }

    @Command("npc displayname <npc> <name>")
    @Permission("fancynpcs.command.npc.displayname")
    public void onCommand(final CommandSender sender, final Npc npc, final @Argument(suggestions = "empty") @Greedy String name) {
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, name, sender).callEvent()) {
            npc.getData().setDisplayName(name);
            npc.updateForAll();
            translator.translate("npc_displayname_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
