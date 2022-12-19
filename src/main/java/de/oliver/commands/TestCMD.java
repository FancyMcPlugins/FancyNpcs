package de.oliver.commands;

import de.oliver.Npc;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        Npc npc = new Npc("§4Notch", "", player.getLocation(), true, true)
                .addEquipment(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(new ItemStack(Material.NETHERITE_SWORD)))
                .setOnClick(p -> p.sendMessage(Component.text("POG")));
        npc.spawn(player);

        return false;
    }
}
