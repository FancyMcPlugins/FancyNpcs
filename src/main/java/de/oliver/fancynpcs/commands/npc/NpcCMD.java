package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class NpcCMD implements CommandExecutor, TabCompleter {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();
    private final AttributeCMD attributeCMD = new AttributeCMD();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // TODO: move all of this into the subcommands

        if (!(sender instanceof Player p)) {
            MessageHelper.error(sender, lang.get("only-players"));
            return null;
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Stream.of("help", "message", "create", "remove", "copy", "skin", "movehere", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "collidable", "list", "turnToPlayer", "type", "attribute")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());

        } else if (args.length == 2 && !args[0].equalsIgnoreCase("create")) {
            suggestions.addAll(FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()
                    .stream()
                    .filter(npc -> !FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() || npc.getData().getCreator().equals(p.getUniqueId()))
                    .map(npc -> npc.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("equipment")) {
            suggestions.addAll(Arrays.stream(NpcEquipmentSlot.values())
                    .map(Enum::name)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("showInTab") || args[0].equalsIgnoreCase("glowing") || args[0].equalsIgnoreCase("turnToPlayer") || args[0].equalsIgnoreCase("collidable"))) {
            suggestions.addAll(Stream.of("true", "false")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("glowingcolor")) {
            suggestions.addAll(NamedTextColor.NAMES.keys().stream()
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("type")) {
            suggestions.addAll(Arrays.stream(EntityType.values())
                    .map(Enum::name)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("playerCommand") || args[0].equalsIgnoreCase("serverCommand"))) {
            suggestions.addAll(Stream.of("none")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("displayName")) {
            suggestions.addAll(Stream.of("<empty>")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        }

        if (!suggestions.isEmpty()) return suggestions;

        if (args.length < 3) {
            return null;
        }

        String subcommand = args[0];
        String name = args[1];
        Npc npc = FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() ?
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, p.getUniqueId()) :
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);

        switch (subcommand.toLowerCase()) {
            case "attribute" -> {
                return attributeCMD.tabcompletion(p, npc, args);
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("fancynpcs.npc.help") && !sender.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(sender, lang.get("no-permission-subcommand"));
                return false;
            }

            MessageHelper.info(sender, lang.get("npc-command-help-header"));
            MessageHelper.info(sender, lang.get("npc-command-help-create"));
            MessageHelper.info(sender, lang.get("npc-command-help-remove"));
            MessageHelper.info(sender, lang.get("npc-command-help-copy"));
            MessageHelper.info(sender, lang.get("npc-command-help-list"));
            MessageHelper.info(sender, lang.get("npc-command-help-skin"));
            MessageHelper.info(sender, lang.get("npc-command-help-type"));
            MessageHelper.info(sender, lang.get("npc-command-help-moveHere"));
            MessageHelper.info(sender, lang.get("npc-command-help-displayName"));
            MessageHelper.info(sender, lang.get("npc-command-help-equipment"));
            MessageHelper.info(sender, lang.get("npc-command-help-message"));
            MessageHelper.info(sender, lang.get("npc-command-help-playerCommand"));
            MessageHelper.info(sender, lang.get("npc-command-help-serverCommand"));
            MessageHelper.info(sender, lang.get("npc-command-help-showInTab"));
            MessageHelper.info(sender, lang.get("npc-command-help-glowing"));
            MessageHelper.info(sender, lang.get("npc-command-help-glowingColor"));
            MessageHelper.info(sender, lang.get("npc-command-help-collidable"));
            MessageHelper.info(sender, lang.get("npc-command-help-turnToPlayer"));
            MessageHelper.info(sender, lang.get("npc-command-help-attribute"));

            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            return new ListCMD().run(sender, null, args);
        }

        if (args.length < 2) {
            MessageHelper.error(sender, lang.get("wrong-usage"));
            return false;
        }

        String subcommand = args[0];
        String name = args[1];
        Npc npc = FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() ?
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, p.getUniqueId()) :
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);

        if (!sender.hasPermission("fancynpcs.npc." + subcommand) && !sender.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(sender, lang.get("no-permission-subcommand"));
            return false;
        }

        switch (subcommand.toLowerCase()) {
            case "create" -> {
                if (!(sender instanceof Player p)) {
                    MessageHelper.error(sender, lang.get("npc-command.only_player"));
                    return false;
                }

                return new CreateCMD().run(p, null, args);
            }

            case "remove" -> {
                return new RemoveCMD().run(sender, npc, args);
            }

            case "copy" -> {
                return new CopyCMD().run(sender, npc, args);
            }

            case "movehere" -> {
                return new MoveHereCMD().run(sender, npc, args);
            }

            case "message" -> {
                return new MessageCMD().run(sender, npc, args);
            }

            case "skin" -> {
                return new SkinCMD().run(sender, npc, args);
            }

            case "displayname" -> {
                return new DisplayNameCMD().run(sender, npc, args);
            }

            case "equipment" -> {
                return new EquipmentCMD().run(sender, npc, args);
            }

            case "servercommand" -> {
                return new ServerCommandCMD().run(sender, npc, args);
            }

            case "playercommand" -> {
                return new PlayerCommandCMD().run(sender, npc, args);
            }

            case "showintab" -> {
                return new ShowInTabCMD().run(sender, npc, args);
            }

            case "glowing" -> {
                return new GlowingCMD().run(sender, npc, args);
            }

            case "glowingcolor" -> {
                return new GlowingColorCMD().run(sender, npc, args);
            }

            case "collidable" -> {
                return new CollidableCMD().run(sender, npc, args);
            }

            case "turntoplayer" -> {
                return new TurnToPlayerCMD().run(sender, npc, args);
            }

            case "type" -> {
                return new TypeCMD().run(sender, npc, args);
            }

            case "attribute" -> {
                return attributeCMD.run(sender, npc, args);
            }

            default -> {
                MessageHelper.error(sender, lang.get("wrong-usage"));
                return false;
            }
        }
    }
}
