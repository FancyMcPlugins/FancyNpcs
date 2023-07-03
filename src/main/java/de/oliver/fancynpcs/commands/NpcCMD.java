package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
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
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (!p.hasPermission("fancynpcs.npc.help") && !p.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(p, "You don't have permission for this subcommand");
                return false;
            }

            MessageHelper.info(sender, "<b>FancyNpcs Plugin help:");
            MessageHelper.info(sender, " - /npc create (name) <dark_gray>- <white>Creates a new npc at your location");
            MessageHelper.info(sender, " - /npc remove (name) <dark_gray>- <white>Removes an npc");
            MessageHelper.info(sender, " - /npc list <dark_gray>- <white>Summary of all npcs");
            MessageHelper.info(sender, " - /npc skin (name) [(skin)] <dark_gray>- <white>Sets the skin for an npc");
            MessageHelper.info(sender, " - /npc type (name) (type) <dark_gray>- <white>Sets the entity type for an npc");
            MessageHelper.info(sender, " - /npc movehere (name) <dark_gray>- <white>Teleports an npc to your location");
            MessageHelper.info(sender, " - /npc displayName (name) (displayName ...) <dark_gray>- <white>Sets the displayname for an npc");
            MessageHelper.info(sender, " - /npc equipment (name) (slot) <dark_gray>- <white>Equips the npc with the item you are holding");
            MessageHelper.info(sender, " - /npc message (name) (message) <dark_gray>- <white>Set NPC message");
            MessageHelper.info(sender, " - /npc playerCommand (name) (command ...) <dark_gray>- <white>Executes the command on a player when interacting");
            MessageHelper.info(sender, " - /npc serverCommand (name) (command ...) <dark_gray>- <white>The command will be executed by the console when someone interacts with the npc");
            MessageHelper.info(sender, " - /npc showInTab (name) (true|false) <dark_gray>- <white>Whether the NPC will be shown in tab-list or not");
            MessageHelper.info(sender, " - /npc glowing (name) (true|false) <dark_gray>- <white>Whether the NPC will glow or not");
            MessageHelper.info(sender, " - /npc glowingColor (name) (color) <dark_gray>- <white>The color of the glowing effect");
            MessageHelper.info(sender, " - /npc turnToPlayer (name) (true|false) <dark_gray>- <white>Whether the NPC will turn to you or not");

            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            if (!p.hasPermission("fancynpcs.npc.list") && !p.hasPermission("fancynpcs.npc.*")) {
                MessageHelper.error(p, "You don't have permission for this subcommand");
                return false;
            }

            MessageHelper.info(sender, "<b>All NPCs:</b>");

            Collection<Npc> allNpcs = FancyNpcs.getInstance().getNpcManager().getAllNpcs();

            if (allNpcs.isEmpty()) {
                MessageHelper.warning(sender, "There are no NPCs. Use '/npc create' to create one");
            } else {
                final DecimalFormat df = new DecimalFormat("#########.##");
                for (Npc npc : allNpcs) {
                    MessageHelper.info(sender, "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'{tp_cmd}'> - {name} ({x}/{y}/{z})</click></hover>"
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
            MessageHelper.error(sender, "Wrong usage: /npc help");
            return false;
        }

        String subcommand = args[0];
        String name = args[1];

        if (!p.hasPermission("fancynpcs.npc." + subcommand) && !p.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(p, "You don't have permission for this subcommand");
            return false;
        }

        switch (subcommand.toLowerCase()) {
            case "create" -> {
                if (FancyNpcs.getInstance().getNpcManager().getNpc(name) != null) {
                    MessageHelper.error(sender, "An npc with that name already exists");
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

                    MessageHelper.success(sender, "Created new NPC");
                } else {
                    MessageHelper.error(sender, "Creation has been cancelled");
                }
            }

            case "remove" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                NpcRemoveEvent npcRemoveEvent = new NpcRemoveEvent(npc, p);
                npcRemoveEvent.callEvent();
                if (!npcRemoveEvent.isCancelled()) {
                    npc.removeForAll();
                    FancyNpcs.getInstance().getNpcManager().removeNpc(npc);
                    MessageHelper.success(sender, "Removed NPC");
                } else {
                    MessageHelper.error(sender, "Removing has been cancelled");
                }
            }

            case "movehere" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                Location location = p.getLocation();

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setLocation(location);
                    npc.update(p);
                    MessageHelper.success(sender, "Moved NPC to your location");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "message" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find NPC");
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
                    MessageHelper.success(sender, "Updated Message");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "skin" -> {
                if (args.length != 3 && args.length != 2) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                String skinName = args.length == 3 ? args[2] : sender.getName();

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, "Npc's type must be Player to do this");
                    return false;
                }

                if (SkinFetcher.SkinType.getType(skinName) == SkinFetcher.SkinType.UUID) {
                    UUID uuid = UUIDFetcher.getUUID(skinName);
                    if (uuid == null) {
                        MessageHelper.error(sender, "Invalid username");
                        return false;
                    }
                    skinName = uuid.toString();
                }

                SkinFetcher skinFetcher = new SkinFetcher(skinName);
                if (!skinFetcher.isLoaded()) {
                    MessageHelper.error(sender, "Could not load skin. Possible causes:");
                    MessageHelper.error(sender, " - Invalid URL (check the url)");
                    MessageHelper.error(sender, " - Rate limit reached (try again later)");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinFetcher, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setSkin(skinFetcher);
                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                    MessageHelper.success(sender, "Updated skin");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "displayname" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
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
                    MessageHelper.success(sender, "Updated display name");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "equipment" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, "Npc's type must be Player to do this");
                    return false;
                }

                String slot = args[2];

                NpcEquipmentSlot equipmentSlot = NpcEquipmentSlot.parse(slot);
                if (equipmentSlot == null) {
                    MessageHelper.error(sender, "Invalid equipment slot");
                    return false;
                }

                ItemStack item = p.getInventory().getItemInMainHand();

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{equipmentSlot, item}, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().addEquipment(equipmentSlot, item);
                    npc.updateForAll();
                    MessageHelper.success(sender, "Updated equipment");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "servercommand" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
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
                    MessageHelper.success(sender, "Updated server command to be executed");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "playercommand" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
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
                    MessageHelper.success(sender, "Updated player command to be executed");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "showintab" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                if (npc.getData().getType() != EntityType.PLAYER) {
                    MessageHelper.error(sender, "Npc's type must be Player to do this");
                    return false;
                }

                boolean showInTab;
                switch (args[2].toLowerCase()) {
                    case "true" -> showInTab = true;
                    case "false" -> showInTab = false;
                    default -> {
                        MessageHelper.error(sender, "Invalid argument (expected: 'true' or 'false')");
                        return false;
                    }
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, showInTab, p);
                npcModifyEvent.callEvent();

                if (showInTab == npc.getData().isShowInTab()) {
                    MessageHelper.warning(sender, "Nothing has changed");
                    return false;
                }

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setShowInTab(showInTab);
                    npc.updateForAll();

                    if (showInTab) {
                        MessageHelper.success(sender, "NPC will now be shown in tab");
                    } else {
                        MessageHelper.success(sender, "NPC will no longer be shown in tab");
                    }
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "glowing" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                boolean glowing;
                try {
                    glowing = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, glowing, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setGlowing(glowing);
                    npc.updateForAll();

                    if (glowing) {
                        MessageHelper.success(sender, "NPC will now glow");
                    } else {
                        MessageHelper.success(sender, "NPC will no longer glow");
                    }
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "glowingcolor" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                NamedTextColor color = NamedTextColor.NAMES.value(args[2]);
                if (color == null) {
                    MessageHelper.error(sender, "Invalid color");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setGlowingColor(color);
                    npc.updateForAll();
                    MessageHelper.success(sender, "Updated glowing color");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }

            }

            case "turntoplayer" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                boolean turnToPlayer;
                try {
                    turnToPlayer = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, turnToPlayer, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.getData().setTurnToPlayer(turnToPlayer);

                    if (turnToPlayer) {
                        MessageHelper.success(sender, "NPC will now turn to the players");
                    } else {
                        MessageHelper.success(sender, "NPC will no longer turn to the players");
                        npc.updateForAll(); // move to default pos
                    }
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "type" -> {
                if (args.length < 3) {
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if (npc == null) {
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                EntityType type = EntityType.fromName(args[2].toLowerCase());

                if (type == null) {
                    MessageHelper.error(sender, "Invalid type");
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
                    MessageHelper.success(sender, "Updated entity type");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            default -> {
                MessageHelper.error(sender, "Wrong usage: /npc help");
                return false;
            }
        }

        return false;
    }
}
