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

    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private static final List<String> NONE_SUGGESTIONS = List.of("@none");

    @Command("npc displayname")
    @Permission("fancynpcs.command.npc.displayname")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_displayname_syntax").send(sender);
    }

    @Command("npc displayname <npc> <name>")
    @Permission("fancynpcs.command.npc.displayname")
    public void onCommand(final CommandSender sender, final Npc npc, final @Argument(suggestions = "DisplayNameCMD/none") @Greedy String name) {
        // Finalizing the name. In case input is '@none', it gets replaced with '<empty>' for backwards compatibility.
        final String finalName = name.equalsIgnoreCase("@none") ? "<empty>" : name;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, finalName, sender).callEvent()) {
            npc.getData().setDisplayName(finalName);
            npc.updateForAll();
            translator.translate(finalName.equalsIgnoreCase("<empty>") ? "npc_displayname_set_empty" : "npc_displayname_set_name")
                    .replace("npc", npc.getData().getName())
                    .replace("name", finalName)
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Suggestions("DisplayNameCMD/none")
    public List<String> suggestions(final CommandContext<CommandSender> sender, CommandInput input) {
        return NONE_SUGGESTIONS;
    }

}
