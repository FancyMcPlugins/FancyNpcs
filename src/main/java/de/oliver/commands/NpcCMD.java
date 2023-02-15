package de.oliver.commands;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import de.oliver.utils.SkinFetcher;
import de.oliver.utils.UUIDFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class NpcCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1){
            return Arrays.asList("help", "create", "remove", "skin", "movehere", "displayName", "equipment", "command", "showInTab", "glowing", "glowingColor");
        } else if(args.length == 2 && !args[0].equalsIgnoreCase("create")){
            return NpcPlugin.getInstance().getNpcManager().getAllNpcs().stream().map(Npc::getName).toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("equipment")){
            return Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::getName).toList();
        } else if(args.length == 3 && (args[0].equalsIgnoreCase("showInTab") || args[0].equalsIgnoreCase("glowing"))){
            return Arrays.asList("true", "false");
        } else if(args.length == 3 && args[0].equalsIgnoreCase("glowingcolor")){
            return Arrays.stream(ChatFormatting.values()).map(ChatFormatting::getName).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green><b>NPC Plugin help:"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc create (name) <dark_gray>- <white>Creates a new npc at your location"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc remove (name) <dark_gray>- <white>Removes an npc"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc skin (name) (skin) <dark_gray>- <white>Sets the skin for an npc"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc movehere (name) <dark_gray>- <white>Teleports an npc to your location"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc displayName (name) (displayName ...) <dark_gray>- <white>Sets the displayname for an npc"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc equipment (name) (slot) <dark_gray>- <white>Equips the npc with the item you are holding"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc command (name) (command ...) <dark_gray>- <white>The command will be executed when someone interacts with the npc"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc showInTab (name) (true|false) <dark_gray>- <white>Whether the NPC will be shown in tab-list or not"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc glowing (name) (true|false) <dark_gray>- <white>Whether the NPC will glow or not"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/npc glowingColor (name) (color) <dark_gray>- <white>The color of the glowing effect"));

            return true;
        }

        if (args.length < 2){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
            return false;
        }

        String subcommand = args[0];
        String name = args[1];

        switch (subcommand.toLowerCase()){
            case "create" -> {
                if(NpcPlugin.getInstance().getNpcManager().getNpc(name) != null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An npc with that name already exists</red>"));
                    return false;
                }

                Npc npc = new Npc(name, p.getLocation());
                npc.create();
                npc.spawnForAll();

                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Created new npc</green>"));
            }

            case "remove" -> {
                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                npc.removeForAll();
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Removed npc</green>"));
            }

            case "movehere" -> {
                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                npc.moveForAll(p.getLocation());
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Moved npc to your location</green>"));
            }

            case "skin" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                String skinName = args[2];

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                SkinFetcher skinFetcher = new SkinFetcher(UUIDFetcher.getUUID(skinName).toString());
                npc.updateSkin(skinFetcher);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated skin of npc</green>"));
            }

            case "displayname" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                String displayName = "";
                for (int i = 2; i < args.length; i++) {
                    displayName += args[i] + " ";
                }
                displayName = displayName.substring(0, displayName.length() - 1);

                displayName = displayName.replace("&", "§");

                npc.updateDisplayName(displayName);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated display name of npc</green>"));
            }

            case "equipment" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                String slot = args[2];

                EquipmentSlot equipmentSlot = null;
                try {
                    equipmentSlot = EquipmentSlot.byName(slot);
                } catch (IllegalArgumentException e){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid equipment slot</red>"));
                    return false;
                }

//                if(p.getInventory().getItemInMainHand().getType() == Material.AIR){
//                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must hold an item in hand</red>"));
//                    return false;
//                }

                ItemStack item = p.getInventory().getItemInMainHand();

                npc.addEquipment(equipmentSlot, CraftItemStack.asNMSCopy(item));
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated equipment of npc</green>"));
            }

            case "command" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                String cmd = "";
                for (int i = 2; i < args.length; i++) {
                    cmd += args[i] + " ";
                }
                cmd = cmd.substring(0, cmd.length() - 1);

                npc.setCommand(cmd);

                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated command to be executed</green>"));
            }

            case "showintab" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                boolean showInTab;
                switch (args[2].toLowerCase()) {
                    case "true" -> showInTab = true;
                    case "false" -> showInTab = false;
                    default -> {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid argument (use 'true' or 'false')</red>"));
                        return false;
                    }
                }

                if(showInTab == npc.isShowInTab()){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Nothing has changed</red>"));
                    return false;
                }

                npc.setShowInTab(showInTab);
                if(!showInTab){
                    npc.removeFromTabForAll();
                } else {
                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                }

                if(showInTab){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>NPC will now be shown in tab</green>"));
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>NPC will no longer be shown in tab</green>"));
                }
            }

            case "glowing" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                boolean glowing;
                try{
                    glowing = Boolean.parseBoolean(args[2]);
                }catch (Exception e){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                npc.setGlowing(glowing);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();

                if(glowing){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>NPC will now glow</green>"));
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>NPC will no glow</green>"));
                }
            }

            case "glowingcolor" -> {
                if(args.length < 3){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                Npc npc = NpcPlugin.getInstance().getNpcManager().getNpc(name);
                if(npc == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find npc</red>"));
                    return false;
                }

                ChatFormatting color = ChatFormatting.getByName(args[2]);
                if(color == null){
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                    return false;
                }

                npc.setGlowingColor(color);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();

                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Updated glowing color to '" + color.getName() + "'</green>"));
            }

            default -> {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                return false;
            }
        }

        return false;
    }
}
