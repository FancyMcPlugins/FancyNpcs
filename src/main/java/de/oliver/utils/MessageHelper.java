package de.oliver.utils;

import de.oliver.FancyNpcConfig;
import de.oliver.FancyNpcs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MessageHelper {

    private final static FancyNpcConfig config = FancyNpcs.getInstance().getFancyNpcConfig();

    public static void info(CommandSender receiver, String message, boolean withPrefix) {
        receiver.sendMessage(MiniMessage.miniMessage().deserialize((withPrefix ? config.getPrefix() : "") + "<color:" + config.getPrimaryColor() + "> " + message));
    }

    public static void info(CommandSender receiver, String message) {
        info(receiver, message, true);
    }

    public static void success(CommandSender receiver, String message, boolean withPrefix) {
        receiver.sendMessage(MiniMessage.miniMessage().deserialize((withPrefix ? config.getPrefix() : "") + "<color:" + config.getSuccessColor() + "> " + message));
    }

    public static void success(CommandSender receiver, String message) {
        success(receiver, message, true);
    }

    public static void warning(CommandSender receiver, String message, boolean withPrefix) {
        receiver.sendMessage(MiniMessage.miniMessage().deserialize((withPrefix ? config.getPrefix() : "") + "<color:" + config.getWarningColor() + "> " + message));
    }

    public static void warning(CommandSender receiver, String message) {
        warning(receiver, message, true);
    }

    public static void error(CommandSender receiver, String message, boolean withPrefix) {
        receiver.sendMessage(MiniMessage.miniMessage().deserialize((withPrefix ? config.getPrefix() : "") + "<color:" + config.getErrorColor() + "> " + message));
    }

    public static void error(CommandSender receiver, String message) {
        error(receiver, message, true);
    }
}
