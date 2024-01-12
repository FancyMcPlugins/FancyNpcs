package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class NpcCMD extends Command {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();
    private final Subcommand attributeCMD = new AttributeCMD();
    private final Subcommand collidableCMD = new CollidableCMD();
    private final Subcommand displayNameCMD = new DisplayNameCMD();
    private final Subcommand equipmentCMD = new EquipmentCMD();
    private final Subcommand glowingCMD = new GlowingCMD();
    private final Subcommand glowingColorCMD = new GlowingColorCMD();
    private final Subcommand messageCMD = new MessageCMD();
    private final Subcommand playerCommandCMD = new PlayerCommandCMD();
    private final Subcommand serverCommandCMD = new ServerCommandCMD();
    private final Subcommand showInTabCMD = new ShowInTabCMD();
    private final Subcommand teleportCMD = new TeleportCMD();
    private final Subcommand turnToPlayerCMD = new TurnToPlayerCMD();
    private final Subcommand typeCMD = new TypeCMD();

    public NpcCMD() {
        super("npc");
        setPermission("fancynpcs.npc");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            MessageHelper.error(sender, lang.get("only-players"));
            return null;
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Stream.of("help", "info", "message", "create", "remove", "copy", "skin", "movehere", "teleport", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "collidable", "list", "turnToPlayer", "type", "attribute", "interactionCooldown")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());

        } else if (args.length == 2 && !args[0].equalsIgnoreCase("create")) {
            suggestions.addAll(FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()
                    .stream()
                    .filter(npc -> !FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() || npc.getData().getCreator().equals(p.getUniqueId()))
                    .map(npc -> npc.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList());
        }

        if (!suggestions.isEmpty()) return suggestions;

        if (args.length < 3) {
            return Collections.emptyList();
        }

        String subcommand = args[0];
        String name = args[1];
        Npc npc = FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() ?
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, p.getUniqueId()) :
                FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);

        return switch (subcommand.toLowerCase()) {
            case "attribute" -> attributeCMD.tabcompletion(p, npc, args);
            case "collidable" -> collidableCMD.tabcompletion(p, npc, args);
            case "displayname" -> displayNameCMD.tabcompletion(p, npc, args);
            case "equipment" -> equipmentCMD.tabcompletion(p, npc, args);
            case "glowing" -> glowingCMD.tabcompletion(p, npc, args);
            case "glowingcolor" -> glowingColorCMD.tabcompletion(p, npc, args);
            case "message" -> messageCMD.tabcompletion(p, npc, args);
            case "playercommand" -> playerCommandCMD.tabcompletion(p, npc, args);
            case "servercommand" -> serverCommandCMD.tabcompletion(p, npc, args);
            case "showintab" -> showInTabCMD.tabcompletion(p, npc, args);
            case "teleport" -> teleportCMD.tabcompletion(p, npc, args);
            case "turntoplayer" -> turnToPlayerCMD.tabcompletion(p, npc, args);
            case "type" -> typeCMD.tabcompletion(p, npc, args);

            default -> Collections.emptyList();
        };
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        
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
            MessageHelper.info(sender, lang.get("npc-command-help-teleport"));
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
            MessageHelper.info(sender, lang.get("npc-command-help-interactionCooldown"));

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
        Npc npc;
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && sender instanceof Player player) {
            npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name, player.getUniqueId());
        } else {
            npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(name);
        }


        if (!sender.hasPermission("fancynpcs.npc." + subcommand) && !sender.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(sender, lang.get("no-permission-subcommand"));
            return false;
        }

        switch (subcommand.toLowerCase()) {
            case "create" -> {
                return new CreateCMD().run(sender, null, args);
            }

            case "remove" -> {
                return new RemoveCMD().run(sender, npc, args);
            }

            case "copy" -> {
                return new CopyCMD().run(sender, npc, args);
            }

            case "info" -> {
                return new InfoCMD().run(sender, npc, args);
            }

            case "movehere" -> {
                return new MoveHereCMD().run(sender, npc, args);
            }

            case "teleport" -> {
                return teleportCMD.run(sender, npc, args);
            }

            case "message" -> {
                return messageCMD.run(sender, npc, args);
            }

            case "skin" -> {
                return new SkinCMD().run(sender, npc, args);
            }

            case "displayname" -> {
                return displayNameCMD.run(sender, npc, args);
            }

            case "equipment" -> {
                return equipmentCMD.run(sender, npc, args);
            }

            case "servercommand" -> {
                return serverCommandCMD.run(sender, npc, args);
            }

            case "playercommand" -> {
                return playerCommandCMD.run(sender, npc, args);
            }

            case "interactioncooldown" -> {
                return new InteractionCooldownCMD().run(sender, npc, args);
            }

            case "showintab" -> {
                return showInTabCMD.run(sender, npc, args);
            }

            case "glowing" -> {
                return glowingCMD.run(sender, npc, args);
            }

            case "glowingcolor" -> {
                return glowingColorCMD.run(sender, npc, args);
            }

            case "collidable" -> {
                return collidableCMD.run(sender, npc, args);
            }

            case "turntoplayer" -> {
                return turnToPlayerCMD.run(sender, npc, args);
            }

            case "type" -> {
                return typeCMD.run(sender, npc, args);
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
