package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.MultiMessage;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public enum HelpCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc help [page]")
    @Permission("fancynpcs.command.npc")
    public void onHelp(
            final @NotNull CommandSender sender,
            final @Argument(suggestions = "HelpCMD/page") @Default("1") int page
    ) {
        // Getting the (full) help contents.
        final MultiMessage contents = (MultiMessage) translator.translate("npc_help_contents");
        // Calculating max page number.
        final int maxPage = (int) Math.ceil(contents.getRawMessages().size() / 6F);
        // Getting the requested page. Defaults to 1 for invalid input and is capped by number of the last page.
        final int finalPage = Math.clamp(page, 1, maxPage);
        // Sending help contents to the sender.
        translator.translate("npc_help_page_header").replace("page", String.valueOf(finalPage)).replace("max_page", String.valueOf(maxPage)).send(sender);
        contents.page(finalPage, 6).send(sender);
        translator.translate("npc_help_page_footer").replace("page", String.valueOf(finalPage)).replace("max_page", String.valueOf(maxPage)).send(sender);
    }

    /* PARSERS AND SUGGESTIONS */

    @Suggestions("HelpCMD/page")
    public List<String> suggestPage(final CommandContext<CommandSender> context, final CommandInput input) {
        // Getting the (full) help contents.
        final MultiMessage contents = (MultiMessage) translator.translate("npc_help_contents");
        // Calculating max page number.
        final int maxPage = contents.getRawMessages().size() / 6 + 1;
        // Returning suggestions...
        return new ArrayList<>() {{
            for (int i = 1; i <= maxPage; i++)
                add(String.valueOf(i));
        }};
    }

}
