package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FancyNpcsCMD extends Command {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    public FancyNpcsCMD() {
        super("fancynpcs");
        setPermission("fancynpcs.admin");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return Stream.of("version", "reload", "save", "featureFlags")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        FancyNpcs plugin = FancyNpcs.getInstance();

        if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
            FancyNpcs.getInstance().getVersionConfig().checkVersionAndDisplay(sender, false);

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.getLanguageConfig().load();
            plugin.getFancyNpcConfig().reload();
            plugin.getNpcManagerImpl().reloadNpcs();
            MessageHelper.success(sender, lang.get("reloaded-config"));

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("save")) {
            plugin.getNpcManagerImpl().saveNpcs(true);
            MessageHelper.success(sender, lang.get("saved-npcs"));

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("featureFlags")) {
            MessageHelper.info(sender, "<b>Feature flags:</b>");
            MessageHelper.info(sender, " - player-npcs: " + FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled());

        } else {
            MessageHelper.info(sender, lang.get("fancynpcs-syntax"));
            return false;
        }

        return true;
    }
}
