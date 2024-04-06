package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.MultiMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public class NpcCMD extends Command {

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
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
    private final Subcommand mirrorSkinCMD = new MirrorSkinCMD();
    private final Subcommand fixCMD = new FixCMD();

    public NpcCMD() {
        super("npc");
        setPermission("fancynpcs.npc");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            translator.translate("command_player_only").send(sender);
            return null;
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Stream.of("help", "info", "message", "create", "remove", "copy", "fix", "skin", "movehere", "teleport", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "collidable", "list", "turnToPlayer", "type", "attribute", "interactionCooldown", "mirrorSkin")
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
            case "mirrorskin" -> mirrorSkinCMD.tabcompletion(p, npc, args);
            case "fix" -> fixCMD.tabcompletion(p, npc, args);

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
                translator.translate("command_missing_permissions").send(sender);
                return false;
            }
            // Getting the (full) help contents.
            final MultiMessage contents = (MultiMessage) translator.translate("npc_help_contents");
            // Calculating max page number.
            final int maxPage = contents.getRawMessages().size() / 6 + 1;
            // Getting the requested page. Defaults to 1 for invalid input and is capped by number of the last page.
            final int page = Math.min(args.length == 2 ? parseIntOrDefault(args[1], 1) : 1, maxPage);
            // Getting help contents for requested page, or defaulting to 1.
            final MultiMessage requestedContents = contents.page(page, 6);
            // Sending help header to the sender.
            translator.translate("npc_help_page_header").replace("page", String.valueOf(page)).replace("max_page", String.valueOf(maxPage)).send(sender);
            // Sending (requested) help contents to the sender.
            requestedContents.send(sender);
            // Sending help footer to the sender.
            translator.translate("npc_help_page_footer").replace("page", String.valueOf(page)).replace("max_page", String.valueOf(maxPage)).send(sender);
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            return new ListCMD().run(sender, null, args);
        }

        if (args.length < 2) {
            translator.translate("command_wrong_usage").send(sender);
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
            translator.translate("command_missing_permissions").send(sender);
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

            case "mirrorskin" -> {
                return mirrorSkinCMD.run(sender, npc, args);
            }

            case "fix" -> {
                return fixCMD.run(sender, npc, args);
            }

            default -> {
                translator.translate("command_wrong_usage").send(sender);
                return false;
            }
        }
    }

    // Parses String to Integer and returns it, or default value if exception was caught.
    private static int parseIntOrDefault(final String str, final int def) {
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            return def;
        }
    }

}
