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

    private final LanguageConfig config = FancyNpcs.getInstance().getLanguageConfig();
    private final AttributeCMD attributeCMD = new AttributeCMD();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // TODO: move all of this into the subcommands

        if (!(sender instanceof Player p)) {
            return null;
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Stream.of("help", "message", "create", "remove", "copy", "skin", "movehere", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "list", "turnToPlayer", "type")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());

            if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                suggestions.addAll(Stream.of("attribute")
                        .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                        .toList());
            }

        } else if (args.length == 2 && !args[0].equalsIgnoreCase("create")) {
            suggestions.addAll(FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()
                    .stream()
                    .map(npc -> npc.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("equipment")) {
            suggestions.addAll(Arrays.stream(NpcEquipmentSlot.values())
                    .map(Enum::name)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList());
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("showInTab") || args[0].equalsIgnoreCase("glowing") || args[0].equalsIgnoreCase("turnToPlayer"))) {
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
        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);

        switch (subcommand.toLowerCase()) {
            case "attribute" -> {
                if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                    return attributeCMD.tabcompletion(p, npc, args);
                }
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player p)) {
            MessageHelper.error(sender, config.get("npc_commands.only_player"));
            return false;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (!p.hasPermission("fancynpcs.npc.help") && !p.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(p, config.get("npc_commands-no_permission"));
                return false;
            }

            MessageHelper.info(p, config.get("npc_commands-help-header"));
            MessageHelper.info(p, config.get("npc_commands-help-create"));
            MessageHelper.info(p, config.get("npc_commands-help-remove"));
            MessageHelper.info(p, config.get("npc_commands-help-copy"));
            MessageHelper.info(p, config.get("npc_commands-help-list"));
            MessageHelper.info(p, config.get("npc_commands-help-skin"));
            MessageHelper.info(p, config.get("npc_commands-help-type"));
            MessageHelper.info(p, config.get("npc_commands-help-moveHere"));
            MessageHelper.info(p, config.get("npc_commands-help-displayName"));
            MessageHelper.info(p, config.get("npc_commands-help-equipment"));
            MessageHelper.info(p, config.get("npc_commands-help-message"));
            MessageHelper.info(p, config.get("npc_commands-help-playerCommand"));
            MessageHelper.info(p, config.get("npc_commands-help-serverCommand"));
            MessageHelper.info(p, config.get("npc_commands-help-showInTab"));
            MessageHelper.info(p, config.get("npc_commands-help-glowing"));
            MessageHelper.info(p, config.get("npc_commands-help-glowingColor"));
            MessageHelper.info(p, config.get("npc_commands-help-turnToPlayer"));
            if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                MessageHelper.info(p, config.get("npc_commands-help-attribute"));
            }

            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            return new ListCMD().run(p, null, args);
        }

        if (args.length < 2) {
            MessageHelper.error(p, config.get("npc_commands-wrong_usage"));
            return false;
        }

        String subcommand = args[0];
        String name = args[1];
        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);

        if (!p.hasPermission("fancynpcs.npc." + subcommand) && !p.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(p, config.get("npc_commands-no_permission"));
            return false;
        }

        switch (subcommand.toLowerCase()) {
            case "create" -> {
                return new CreateCMD().run(p, null, args);
            }

            case "remove" -> {
                return new RemoveCMD().run(p, npc, args);
            }

            case "copy" -> {
                return new CopyCMD().run(p, npc, args);
            }

            case "movehere" -> {
                return new MoveHereCMD().run(p, npc, args);
            }

            case "message" -> {
                return new MessageCMD().run(p, npc, args);
            }

            case "skin" -> {
                return new SkinCMD().run(p, npc, args);
            }

            case "displayname" -> {
                return new DisplayNameCMD().run(p, npc, args);
            }

            case "equipment" -> {
                return new EquipmentCMD().run(p, npc, args);
            }

            case "servercommand" -> {
                return new ServerCommandCMD().run(p, npc, args);
            }

            case "playercommand" -> {
                return new PlayerCommandCMD().run(p, npc, args);
            }

            case "showintab" -> {
                return new ShowInTabCMD().run(p, npc, args);
            }

            case "glowing" -> {
                return new GlowingCMD().run(p, npc, args);
            }

            case "glowingcolor" -> {
                return new GlowingColorCMD().run(p, npc, args);
            }

            case "turntoplayer" -> {
                return new TurnToPlayerCMD().run(p, npc, args);
            }

            case "type" -> {
                return new TypeCMD().run(p, npc, args);
            }

            case "attribute" -> {
                if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                    return attributeCMD.run(p, npc, args);
                }
            }

            default -> {
                MessageHelper.error(p, config.get("npc_commands-wrong_usage"));
                return false;
            }
        }

        MessageHelper.error(p, config.get("npc_commands-wrong_usage"));
        return false;
    }
}
