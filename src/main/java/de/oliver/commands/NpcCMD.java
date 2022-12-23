package de.oliver.commands;

import de.oliver.Npc;
import de.oliver.NpcPlugin;
import de.oliver.utils.SkinFetcher;
import de.oliver.utils.UUIDFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            return Arrays.asList("create", "remove", "skin", "movehere", "displayName", "equipment");
        } else if(args.length == 2 && !args[0].equalsIgnoreCase("create")){
            return NpcPlugin.getInstance().getNpcManager().getAllNpcs().stream().map(Npc::getName).toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("equipment")){
            return Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::getName).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
        }

        if (args.length < 2){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
            return false;
        }

        String subcommand = args[0];
        String name = args[1];

        switch (subcommand.toLowerCase()){
            case "create" -> {
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

                if(displayName.length() > 16){
                    displayName = displayName.substring(0, 16);
                }

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

            default -> {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /npc help</red>"));
                return false;
            }
        }

        return false;
    }
}
