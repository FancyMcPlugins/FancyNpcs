package de.oliver.commands;

import de.oliver.FancyNpcs;
import de.oliver.Npc;
import de.oliver.events.NpcCreateEvent;
import de.oliver.events.NpcModifyEvent;
import de.oliver.events.NpcRemoveEvent;
import de.oliver.utils.MessageHelper;
import de.oliver.utils.SkinFetcher;
import de.oliver.utils.UUIDFetcher;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class NpcCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1){
            return Stream.of("help", "create", "remove", "skin", "movehere", "displayName", "equipment", "playerCommand", "serverCommand", "showInTab", "glowing", "glowingColor", "list", "turnToPlayer")
                    .filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        } else if(args.length == 2 && !args[0].equalsIgnoreCase("create")){
            return FancyNpcs.getInstance().getNpcManager().getAllNpcs()
                    .stream()
                    .map(Npc::getName)
                    .filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("equipment")){
            return Arrays.stream(EquipmentSlot.values())
                    .map(EquipmentSlot::getName)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        } else if(args.length == 3 && (args[0].equalsIgnoreCase("showInTab") || args[0].equalsIgnoreCase("glowing") || args[0].equalsIgnoreCase("turnToPlayer"))){
            return Stream.of("true", "false")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("glowingcolor")){
            return Arrays.stream(ChatFormatting.values())
                    .map(ChatFormatting::getName)
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
            MessageHelper.info(sender, "<b>FancyNpcs Plugin help:");
            MessageHelper.info(sender, " - /npc create (name) <dark_gray>- <white>Creates a new npc at your location", false);
            MessageHelper.info(sender, " - /npc remove (name) <dark_gray>- <white>Removes an npc", false);
            MessageHelper.info(sender, " - /npc list <dark_gray>- <white>Summary of all npcs", false);
            MessageHelper.info(sender, " - /npc skin (name) (skin) <dark_gray>- <white>Sets the skin for an npc", false);
            MessageHelper.info(sender, " - /npc movehere (name) <dark_gray>- <white>Teleports an npc to your location", false);
            MessageHelper.info(sender, " - /npc displayName (name) (displayName ...) <dark_gray>- <white>Sets the displayname for an npc", false);
            MessageHelper.info(sender, " - /npc equipment (name) (slot) <dark_gray>- <white>Equips the npc with the item you are holding", false);
            MessageHelper.info(sender, " - /npc playerCommand (name) (command ...) <dark_gray>- <white>Executes the command on a player when interacting", false);
            MessageHelper.info(sender, " - /npc serverCommand (name) (command ...) <dark_gray>- <white>The command will be executed by the console when someone interacts with the npc", false);
            MessageHelper.info(sender, " - /npc showInTab (name) (true|false) <dark_gray>- <white>Whether the NPC will be shown in tab-list or not", false);
            MessageHelper.info(sender, " - /npc glowing (name) (true|false) <dark_gray>- <white>Whether the NPC will glow or not", false);
            MessageHelper.info(sender, " - /npc glowingColor (name) (color) <dark_gray>- <white>The color of the glowing effect", false);
            MessageHelper.info(sender, " - /npc turnToPlayer (name) (true|false) <dark_gray>- <white>Whether the NPC will turn to you or not", false);

            return true;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("list")){
            MessageHelper.info(sender, "<b>All NPCs:</b>");

            Collection<Npc> allNpcs = FancyNpcs.getInstance().getNpcManager().getAllNpcs();

            if(allNpcs.isEmpty()){
                MessageHelper.warning(sender, "There are no NPCs. Use '/npc create' to create one");
            } else {
                final DecimalFormat df = new DecimalFormat("#########.##");
                for (Npc npc : allNpcs) {
                    MessageHelper.info(sender, "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'{tp_cmd}'> - {name} ({x}/{y}/{z})</click></hover>"
                            .replace("{name}", npc.getName())
                            .replace("{x}", df.format(npc.getLocation().x()))
                            .replace("{y}", df.format(npc.getLocation().y()))
                            .replace("{z}", df.format(npc.getLocation().z()))
                            .replace("{tp_cmd}", "/tp " + npc.getLocation().x() + " " + npc.getLocation().y() + " " + npc.getLocation().z())
                    );
                }
            }

            return true;
        }

        if (args.length < 2){
            MessageHelper.error(sender, "Wrong usage: /npc help");
            return false;
        }

        String subcommand = args[0];
        String name = args[1];

        switch (subcommand.toLowerCase()){
            case "create" -> {
                if(FancyNpcs.getInstance().getNpcManager().getNpc(name) != null){
                    MessageHelper.error(sender, "An npc with that name already exists");
                    return false;
                }

                Npc npc = new Npc(name, p.getLocation());
                NpcCreateEvent npcCreateEvent = new NpcCreateEvent(npc, p);
                npcCreateEvent.callEvent();
                if(!npcCreateEvent.isCancelled()){
                    npc.create();
                    npc.spawnForAll();

                    MessageHelper.success(sender, "Created new NPC");
                } else {
                    MessageHelper.error(sender, "Creation has been cancelled");
                }
            }

            case "remove" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                NpcRemoveEvent npcRemoveEvent = new NpcRemoveEvent(npc, p);
                npcRemoveEvent.callEvent();
                if(!npcRemoveEvent.isCancelled()){
                    npc.removeForAll();
                    MessageHelper.success(sender, "Removed NPC");
                } else {
                    MessageHelper.error(sender, "Removing has been cancelled");
                }
            }

            case "movehere" -> {
                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.moveForAll(p.getLocation());
                    MessageHelper.success(sender, "Moved NPC to your location");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "skin" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                String skinName = args[2];

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find NPC");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    SkinFetcher skinFetcher = new SkinFetcher(UUIDFetcher.getUUID(skinName).toString());
                    npc.updateSkin(skinFetcher);
                    MessageHelper.success(sender, "Updated skin");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "displayname" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                String displayName = "";
                for (int i = 2; i < args.length; i++) {
                    displayName += args[i] + " ";
                }
                displayName = displayName.substring(0, displayName.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.updateDisplayName(displayName);
                    MessageHelper.success(sender, "Updated display name");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "equipment" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                String slot = args[2];

                EquipmentSlot equipmentSlot = null;
                try {
                    equipmentSlot = EquipmentSlot.byName(slot);
                } catch (IllegalArgumentException e){
                    MessageHelper.error(sender, "Invalid equipment slot");
                    return false;
                }

                ItemStack item = p.getInventory().getItemInMainHand();

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.addEquipment(equipmentSlot, CraftItemStack.asNMSCopy(item));
                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                    MessageHelper.success(sender, "Updated equipment");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "servercommand" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                String cmd = "";
                for (int i = 2; i < args.length; i++) {
                    cmd += args[i] + " ";
                }
                cmd = cmd.substring(0, cmd.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SERVER_COMMAND, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.setServerCommand(cmd);
                    MessageHelper.success(sender, "Updated server command to be executed");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "playercommand" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                String cmd = "";
                for (int i = 2; i < args.length; i++) {
                    cmd += args[i] + " ";
                }
                cmd = cmd.substring(0, cmd.length() - 1);

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.PLAYER_COMMAND, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.setPlayerCommand(cmd);
                    MessageHelper.success(sender, "Updated player command to be executed");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }
            }

            case "showintab" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
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

                if(showInTab == npc.isShowInTab()){
                    MessageHelper.warning(sender, "Nothing has changed");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.updateShowInTab(showInTab);

                    if(showInTab){
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

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, p);
                npcModifyEvent.callEvent();

                if (!npcModifyEvent.isCancelled()) {
                    npc.updateGlowing(glowing);

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
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                ChatFormatting color = ChatFormatting.getByName(args[2]);
                if(color == null){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.updateGlowingColor(color);
                    MessageHelper.success(sender, "Updated glowing color");
                } else {
                    MessageHelper.error(sender, "Modification has been cancelled");
                }

            }

            case "turntoplayer" -> {
                if(args.length < 3){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    MessageHelper.error(sender, "Could not find npc");
                    return false;
                }

                boolean turnToPlayer;
                try{
                    turnToPlayer = Boolean.parseBoolean(args[2]);
                }catch (Exception e){
                    MessageHelper.error(sender, "Wrong usage: /npc help");
                    return false;
                }

                NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, p);
                npcModifyEvent.callEvent();

                if(!npcModifyEvent.isCancelled()){
                    npc.setTurnToPlayer(turnToPlayer);

                    if(turnToPlayer){
                        MessageHelper.success(sender, "NPC will now turn to the players");
                    } else {
                        MessageHelper.success(sender, "NPC will no longer turn to the players");
                        npc.moveForAll(npc.getLocation()); // move to default pos
                    }
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
