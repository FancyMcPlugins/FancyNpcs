package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcMessagesConfig;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class NpcCMD implements CommandExecutor, TabCompleter {

    private final FancyNpcMessagesConfig config = FancyNpcs.getInstance().getMessagesConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return Stream.of("help", "message", "create", "remove", "skin", "movehere", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "list", "turnToPlayer", "type")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("create")) {
            return FancyNpcs.getInstance().getNpcManager().getAllNpcs()
                    .stream()
                    .map(npc -> npc.getData().getName())
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        } else if (args.length == 3 && args[0].equalsIgnoreCase("equipment")) {
            return Arrays.stream(NpcEquipmentSlot.values())
                    .map(Enum::name)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("showInTab") || args[0].equalsIgnoreCase("glowing") || args[0].equalsIgnoreCase("turnToPlayer"))) {
            return Stream.of("true", "false")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        } else if (args.length == 3 && args[0].equalsIgnoreCase("glowingcolor")) {
            return NamedTextColor.NAMES.keys().stream()
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        } else if (args.length == 3 && args[0].equalsIgnoreCase("type")) {
            return Arrays.stream(EntityType.values())
                    .map(Enum::name)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player p)) {
            MessageHelper.error(sender, config.getString("npc_commands.only_player"));
            return false;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (!p.hasPermission("fancynpcs.npc.help") && !p.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(p, config.getString("npc_commands.no_permission"));
                return false;
            }

            MessageHelper.info(sender, config.getString("npc_commands.help.header"));
            MessageHelper.info(sender, config.getString("npc_commands.help.create"));
            MessageHelper.info(sender, config.getString("npc_commands.help.remove"));
            MessageHelper.info(sender, config.getString("npc_commands.help.list"));
            MessageHelper.info(sender, config.getString("npc_commands.help.skin"));
            MessageHelper.info(sender, config.getString("npc_commands.help.type"));
            MessageHelper.info(sender, config.getString("npc_commands.help.moveHere"));
            MessageHelper.info(sender, config.getString("npc_commands.help.displayName"));
            MessageHelper.info(sender, config.getString("npc_commands.help.equipment"));
            MessageHelper.info(sender, config.getString("npc_commands.help.message"));
            MessageHelper.info(sender, config.getString("npc_commands.help.playerCommand"));
            MessageHelper.info(sender, config.getString("npc_commands.help.serverCommand"));
            MessageHelper.info(sender, config.getString("npc_commands.help.showInTab"));
            MessageHelper.info(sender, config.getString("npc_commands.help.glowing"));
            MessageHelper.info(sender, config.getString("npc_commands.help.glowingColor"));
            MessageHelper.info(sender, config.getString("npc_commands.help.turnToPlayer"));

            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            if (!p.hasPermission("fancynpcs.npc.list") && !p.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(p, config.getString("npc_commands.no_permission"));
                return false;
            }

            MessageHelper.info(sender, config.getString("npc_commands.list.header"));

            Collection<Npc> allNpcs = FancyNpcs.getInstance().getNpcManager().getAllNpcs();

            if (allNpcs.isEmpty()) {
                MessageHelper.warning(sender, config.getString("npc_commands.list.no_npcs"));
            } else {
                final DecimalFormat df = new DecimalFormat("#########.##");
                for (Npc npc : allNpcs) {
                    MessageHelper.info(sender, config.getString("npc_commands.list.info")
                            .replace("{name}", npc.getData().getName())
                            .replace("{x}", df.format(npc.getData().getLocation().x()))
                            .replace("{y}", df.format(npc.getData().getLocation().y()))
                            .replace("{z}", df.format(npc.getData().getLocation().z()))
                            .replace("{tp_cmd}", "/tp " + npc.getData().getLocation().x() + " " + npc.getData().getLocation().y() + " " + npc.getData().getLocation().z())
                    );
                }
            }

            return true;
        }

        if (args.length < 2) {
            MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
            return false;
        }

        String subcommand = args[0];
        String name = args[1];

        if (!p.hasPermission("fancynpcs.npc." + subcommand) && !p.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(p, config.getString("npc_commands.no_permission"));
            return false;
        }

        switch (subcommand.toLowerCase()) {
            case "create" -> {
                if (FancyNpcs.getInstance().getNpcManager().getNpc(name) != null) {
                    MessageHelper.error(sender, config.getString("npc_commands.create.exist"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(name, p.getLocation()));
                npc.getData().setLocation(p.getLocation());

                NpcCreateEvent npcCreateEvent = new NpcCreateEvent(npc, p);
                npcCreateEvent.callEvent();
                if (!npcCreateEvent.isCancelled()) {
                    npc.create();
                    FancyNpcs.getInstance().getNpcManager().registerNpc(npc);
                    npc.spawnForAll();

                    MessageHelper.success(sender, config.getString("npc_commands.create.created"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.create.failed"));
                }
            }

            case "remove" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                NpcRemoveEvent npcRemoveEvent = new NpcRemoveEvent(npc, p);
                npcRemoveEvent.callEvent();
                if (!npcRemoveEvent.isCancelled()) {
                    npc.removeForAll();
                    FancyNpcs.getInstance().getNpcManager().removeNpc(npc);
                    MessageHelper.success(sender, config.getString("npc_commands.remove.removed"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.remove.failed"));
                }
            }

            case "movehere" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                Location location = p.getLocation();

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setLocation(location);
                    npc.update(p);
                    MessageHelper.success(sender, config.getString("npc_commands.moveHere.moved"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.moveHere.failed"));
                }
            }

            case "message" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                String message = "";
                for (int i = 2; i < args.length; i++) {
                    message += args[i] + " ";
                }

                message = message.substring(0, message.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.CUSTOM_MESSAGE, message, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setMessage(message);
                    MessageHelper.success(sender, config.getString("npc_commands.message.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.message.failed"));
                }
            }

            case "skin" -> {
                if (args.length != 3 && args.length != 2) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                String skinName = args.length == 3 ? args[2] : sender.getName();

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, config.getString("npc_commands.must_player"));
                    return false;
                }

                if (SkinFetcher.SkinType.getType(skinName) == SkinFetcher.SkinType.UUID) {
                    UUID uuid = UUIDFetcher.getUUID(skinName);
                    if (uuid == null) {
                        MessageHelper.error(sender, config.getString("npc_commands.skin.invalid"));
                        return false;
                    }
                    skinName = uuid.toString();
                }

                SkinFetcher skinFetcher = new SkinFetcher(skinName);
                if (!skinFetcher.isLoaded()) {
                    MessageHelper.error(sender, config.getString("npc_commands.message.failed_header"));
                    MessageHelper.error(sender, config.getString("npc_commands.skin.failed_url"));
                    MessageHelper.error(sender, config.getString("npc_commands.skin.failed_limited"));
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setSkin(skinFetcher);
                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                    MessageHelper.success(sender, config.getString("npc_commands.skin.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.skin.failed"));
                }
            }

            case "displayname" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                String displayName = "";
                for (int i = 2; i < args.length; i++) {
                    displayName += args[i] + " ";
                }
                displayName = displayName.substring(0, displayName.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, displayName, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setDisplayName(displayName.toString());
                    npc.updateForAll();
                    MessageHelper.success(sender, config.getString("npc_commands.displayName.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.displayName.failed"));
                }
            }

            case "equipment" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, config.getString("npc_commands.must_player"));
                    return false;
                }

                String slot = args[2];

                NpcEquipmentSlot equipmentSlot = NpcEquipmentSlot.parse(slot);
                if (equipmentSlot == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.equipment.invalid"));
                    return false;
                }

                ItemStack item = p.getInventory().getItemInMainHand();

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{equipmentSlot, item}, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().addEquipment(equipmentSlot, item);
                    npc.updateForAll();
                    MessageHelper.success(sender, config.getString("npc_commands.equipment.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.equipment.failed"));
                }
            }

            case "servercommand" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                String cmd = "";
                for (int i = 2; i < args.length; i++) {
                    cmd += args[i] + " ";
                }
                cmd = cmd.substring(0, cmd.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND, cmd, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setServerCommand(cmd);
                    MessageHelper.success(sender, config.getString("npc_commands.serverCommand.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.serverCommand.failed"));
                }
            }

            case "playercommand" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                String cmd = "";
                for (int i = 2; i < args.length; i++) {
                    cmd += args[i] + " ";
                }
                cmd = cmd.substring(0, cmd.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, cmd, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setPlayerCommand(cmd);
                    MessageHelper.success(sender, config.getString("npc_commands.playerCommand.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.playerCommand.failed"));
                }
            }

            case "showintab" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, config.getString("npc_commands.must_player"));
                    return false;
                }

                boolean showInTab;
                switch (args[2].toLowerCase()) {
                    case "true" -> showInTab = true;
                    case "false" -> showInTab = false;
                    default -> {
                        MessageHelper.error(sender, config.getString("npc_commands.showInTab.invalid"));
                        return false;
                    }
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, showInTab, p);
                npcModifyEvent.callEvent();

                if (showInTab == npc.getData().isShowInTab()) {
                    MessageHelper.warning(sender, config.getString("npc_commands.showInTab.same"));
                    return false;
                }

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setShowInTab(showInTab);
                    npc.updateForAll();

                    if (showInTab) {
                        MessageHelper.success(sender, config.getString("npc_commands.showInTab.on"));
                    } else {
                        MessageHelper.success(sender, config.getString("npc_commands.showInTab.off"));
                    }
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.showInTab.failed"));
                }
            }

            case "glowing" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, config.getString("npc_commands.must_player"));
                    return false;
                }

                boolean glowing;
                try {
                    glowing = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, glowing, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setGlowing(glowing);
                    npc.updateForAll();

                    if (glowing) {
                        MessageHelper.success(sender, config.getString("npc_commands.glowing.on"));
                    } else {
                        MessageHelper.success(sender, config.getString("npc_commands.glowing.off"));
                    }
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.glowing.failed"));
                }
            }

            case "glowingcolor" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, config.getString("npc_commands.must_player"));
                    return false;
                }

                NamedTextColor color = NamedTextColor.NAMES.value(args[2]);
                if (color == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.glowingColor.invalid"));
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setGlowingColor(color);
                    npc.updateForAll();
                    MessageHelper.success(sender, config.getString("npc_commands.glowingColor.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.glowingColor.failed"));
                }

            }

            case "turntoplayer" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                boolean turnToPlayer;
                try {
                    turnToPlayer = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, turnToPlayer, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setTurnToPlayer(turnToPlayer);

                    if (turnToPlayer) {
                        MessageHelper.success(sender, config.getString("npc_commands.turnToPlayer.on"));
                    } else {
                        MessageHelper.success(sender, config.getString("npc_commands.turnToPlayer.off"));
                        npc.updateForAll(); // move to default pos
                    }
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.turnToPlayer.failed"));
                }
            }

            case "type" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.not_found"));
                    return false;
                }

                EntityType type = EntityType.fromName(args[2].toLowerCase());

                if (type == null) {
                    MessageHelper.error(sender, config.getString("npc_commands.type.invalid"));
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TYPE, type, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setType(type);

                    if (type != EntityType.PLAYER) {
                        npc.getData().setGlowing(false);
                        npc.getData().setShowInTab(false);
                        if (npc.getData().getEquipment() != null) {
                            npc.getData().getEquipment().clear();
                        }
                    }

                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                    MessageHelper.success(sender, config.getString("npc_commands.type.updated"));
                } else {
                    MessageHelper.error(sender, config.getString("npc_commands.type.failed"));
                }
            }

            default -> {
                MessageHelper.error(sender, config.getString("npc_commands.wrong_usage"));
                return false;
            }
        }

        return false;
    }
}
