package de.oliver.fancynpcs.nms;

import io.netty.channel.Channel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class v1_20_R0 implements NmsBase{

    private static final List<EntityType<?>> excludedTypes = new ArrayList<>();
    public static Map<String, EntityType<?>> TYPES = new HashMap<>();

    static {
        excludedTypes.add(EntityType.AREA_EFFECT_CLOUD);
        excludedTypes.add(EntityType.BLOCK_DISPLAY);
        excludedTypes.add(EntityType.ARROW);
        excludedTypes.add(EntityType.EGG);
        excludedTypes.add(EntityType.ENDER_PEARL);
        excludedTypes.add(EntityType.EVOKER_FANGS);
        excludedTypes.add(EntityType.EXPERIENCE_BOTTLE);
        excludedTypes.add(EntityType.EXPERIENCE_ORB);
        excludedTypes.add(EntityType.FALLING_BLOCK);
        excludedTypes.add(EntityType.FIREWORK_ROCKET);
        excludedTypes.add(EntityType.FISHING_BOBBER);
        excludedTypes.add(EntityType.INTERACTION);
        excludedTypes.add(EntityType.ITEM);
        excludedTypes.add(EntityType.ITEM_DISPLAY);
        excludedTypes.add(EntityType.LIGHTNING_BOLT);
        excludedTypes.add(EntityType.LLAMA_SPIT);
        excludedTypes.add(EntityType.MARKER);
        excludedTypes.add(EntityType.PAINTING);
        excludedTypes.add(EntityType.POTION);
        excludedTypes.add(EntityType.SPECTRAL_ARROW);
        excludedTypes.add(EntityType.TEXT_DISPLAY);
        excludedTypes.add(EntityType.TNT);
        excludedTypes.add(EntityType.TRIDENT);
    }


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

    @Override
    public Map<String, EntityType<?>> getAllEntityTypes() {
        if(TYPES.size() > 0){
            return TYPES;
        }

        for (Field field : net.minecraft.world.entity.EntityType.class.getFields()) {
            try {
                field.setAccessible(true);
                Object possibleType = field.get(null);
                if (!(possibleType instanceof net.minecraft.world.entity.EntityType<?> type)) {
                    continue;
                }

                if (excludedTypes.contains(type)) {
                    continue;
                }

                TYPES.put(type.toShortString(), type);
            } catch (Exception ex) {
            }
        }

        return TYPES;
    }
}
