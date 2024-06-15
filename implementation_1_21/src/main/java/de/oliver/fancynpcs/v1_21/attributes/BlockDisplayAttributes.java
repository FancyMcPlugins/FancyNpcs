package de.oliver.fancynpcs.v1_21.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class BlockDisplayAttributes {

    private static final List<String> BLOCKS = Registry.MATERIAL.stream().filter(Material::isBlock).map(it -> it.key().value()).toList();

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "block",
                BLOCKS,
                List.of(EntityType.BLOCK_DISPLAY),
                BlockDisplayAttributes::setBlock
        ));

        return attributes;
    }

    private static void setBlock(Npc npc, String value) {
        Display.BlockDisplay display = ReflectionHelper.getEntity(npc);

        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(value.toLowerCase()));

        display.setBlockState(block.defaultBlockState());
    }
}
