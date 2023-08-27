package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class FancyNpcsCMD implements CommandExecutor, TabCompleter {

    private final LanguageConfig config = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return Stream.of("version", "reload", "save", "featureFlags")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FancyNpcs plugin = FancyNpcs.getInstance();

        if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
            MessageHelper.info(sender, config.get("commands-version-checking"));
            new Thread(() -> {
                ComparableVersion newestVersion = plugin.getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if (newestVersion == null) {
                    MessageHelper.error(sender, config.get("commands-version-failed"));
                } else if (newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(sender, (
                            config.get("commands-version-outdated")
                                    + "\n"
                                    + config.get("commands-version-download", "new_version", newestVersion.toString(), "download_url", plugin.getVersionFetcher().getDownloadUrl())
                    ));
                } else {
                    MessageHelper.success(sender, config.get("commands-version-latest", "current_version", currentVersion.toString()));
                }
            }).start();
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.getLanguageConfig().load();
            plugin.getFancyNpcConfig().reload();
            plugin.getNpcManagerImpl().reloadNpcs();
            MessageHelper.success(sender, config.get("commands-reload"));
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("save")) {
            plugin.getNpcManagerImpl().saveNpcs(true);
            MessageHelper.success(sender, config.get("commands-save"));
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("featureFlags")) {
            MessageHelper.info(sender, "<b>Feature flags:</b>");
            MessageHelper.info(sender, " - npc-attributes: " + FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled());
            MessageHelper.info(sender, " - player-npcs: " + FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled());
        } else {
            MessageHelper.info(sender, config.get("commands-help"));
            return false;
        }

        return true;
    }
}
