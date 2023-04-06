package de.oliver.commands;

import de.oliver.FancyNpcs;
import de.oliver.utils.MessageHelper;
import de.oliver.utils.VersionFetcher;
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

        if(args.length == 1){
            return Stream.of("version", "reload", "save").filter(input -> input.startsWith(args[0].toLowerCase())).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1 && args[0].equalsIgnoreCase("version")){
            MessageHelper.info(sender, "<i>Checking version, please wait...</i>");
            new Thread(() -> {
                ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if(newestVersion == null){
                    MessageHelper.error(sender, "Could not find latest version");
                } else if(newestVersion.compareTo(currentVersion) > 0){
                    MessageHelper.warning(sender, "You are using an outdated version of the FancyNpcs Plugin");
                    MessageHelper.warning(sender, "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>");
                } else {
                    MessageHelper.success(sender, "You are using the latest version of the FancyNpcs Plugin (" + currentVersion + ")");
                }
            }).start();
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("reload")){
            FancyNpcs.getInstance().getFancyNpcConfig().reload();
            FancyNpcs.getInstance().getNpcManager().reloadNpcs();
            MessageHelper.success(sender, "Reloaded the config");
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("save")){
            FancyNpcs.getInstance().getNpcManager().saveNpcs(true);
            MessageHelper.success(sender, "Saved all NPCs");
        }

        return false;
    }
}
