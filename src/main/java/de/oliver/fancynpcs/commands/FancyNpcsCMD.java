package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public class FancyNpcsCMD extends Command {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

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
        if (!testPermission(sender))
            return false;

        final FancyNpcs plugin = FancyNpcs.getInstance();

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "version" -> FancyNpcs.getInstance().getVersionConfig().checkVersionAndDisplay(sender, false);
                case "reload" -> {
                    translator.loadLanguages(plugin.getDataFolder().getAbsolutePath());
                    plugin.getFancyNpcConfig().reload();
                    plugin.getNpcManagerImpl().reloadNpcs();
                    translator.translate("fancynpcs_reload_success").send(sender);
                }
                case "save" -> {
                    plugin.getNpcManagerImpl().saveNpcs(true);
                    translator.translate("fancynpcs_save_success").send(sender);
                }
                case "featureflags" -> {
                    translator.translate("<gray>Feature Flags:").send(sender);
                    translator.translate("<dark_gray>› Player NPCs<gray>: " + getFormattedBoolean(FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled())).send(sender);
                }
            }
            return true;
        }
        translator.translate("fancynpcs_syntax").send(sender);
        return false;
    }

    private static @NotNull String getFormattedBoolean(final boolean bool) {
        return (bool) ? "<successColor>ON" : "<errorColor>OFF";
    }

}
