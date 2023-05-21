package de.oliver.fancynpcs.nms;

import io.netty.channel.Channel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class v1_19_R2 implements NmsBase{

    @Override
    public MinecraftServer getMinecraftServer(Server server) {
        return ((CraftServer)server).getServer();
    }

    @Override
    public ServerLevel getServerLevel(World world) {
        return ((CraftWorld)world).getHandle();
    }

    @Override
    public ServerPlayer getServerPlayer(Player player) {
        return ((CraftPlayer)player).getHandle();
    }

    @Override
    public Channel getChannel(Player player) {
        return getServerPlayer(player).connection.connection.channel;
    }

    @Override
    public ItemStack getBukkitItemStack(net.minecraft.world.item.ItemStack itemStack) {
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public net.minecraft.world.item.ItemStack getNmsItemStack(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }
}
