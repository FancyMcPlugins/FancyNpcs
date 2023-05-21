package de.oliver.fancynpcs.nms;

import io.netty.channel.Channel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface NmsBase {

    MinecraftServer getMinecraftServer(Server server);
    ServerLevel getServerLevel(World world);
    ServerPlayer getServerPlayer(Player player);
    ItemStack getBukkitItemStack(net.minecraft.world.item.ItemStack itemStack);
    net.minecraft.world.item.ItemStack getNmsItemStack(ItemStack itemStack);
    Channel getChannel(Player player);
    Map<String, EntityType<?>> getAllEntityTypes();

}
