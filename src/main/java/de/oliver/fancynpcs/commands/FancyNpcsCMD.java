package de.oliver.fancynpcs.commands;

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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return Stream.of("version", "reload", "save")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FancyNpcs plugin = FancyNpcs.getInstance();

        if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
            MessageHelper.info(sender, "<i>Checking version, please wait...</i>");
            new Thread(() -> {
                ComparableVersion newestVersion = plugin.getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if (newestVersion == null) {
                    MessageHelper.error(sender, "Could not find latest version");
                } else if (newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(sender, """
                            You are using an outdated version of the FancyHolograms Plugin
                            [!] Please download the newest version (%s): <click:open_url:'%s'><u>click here</u></click>
                            """.formatted(newestVersion, plugin.getVersionFetcher().getDownloadUrl()));
                } else {
                    MessageHelper.success(sender, "You are using the latest version of the FancyNpcs Plugin (" + currentVersion + ")");
                }
            }).start();
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.getFancyNpcConfig().reload();
            plugin.getNpcManager().reloadNpcs();
            MessageHelper.success(sender, "Reloaded the config");
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("save")) {
            plugin.getNpcManager().saveNpcs(true);
            MessageHelper.success(sender, "Saved all NPCs");
        } else {
            MessageHelper.info(sender, "/FancyNpcs <version|reload|save>");
            return false;
        }

        return true;
    }
}
