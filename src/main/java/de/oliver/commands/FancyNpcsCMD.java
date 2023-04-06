package de.oliver.commands;

import de.oliver.FancyNpcs;
import de.oliver.utils.VersionFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            return Stream.of("reload", "version").filter(input -> input.startsWith(args[0].toLowerCase())).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length >= 1 && args[0].equalsIgnoreCase("version")){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#54f790><i>Checking version, please wait...</i></color>"));
            new Thread(() -> {
                ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyNpcs.getInstance().getDescription().getVersion());
                if(newestVersion == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#f25050>Could not find latest version</color>"));
                } else if(newestVersion.compareTo(currentVersion) > 0){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] You are using an outdated version of the FancyNpcs Plugin.</color>"));
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>.</color>"));
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#54f790>You are using the latest version of the FancyNpcs Plugin (" + currentVersion + ").</color>"));
                }
            }).start();
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("reload")){
            FancyNpcs.getInstance().getNpcManager().reloadNpcs();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Reloaded the config</green>"));
        }

        return false;
    }
}
