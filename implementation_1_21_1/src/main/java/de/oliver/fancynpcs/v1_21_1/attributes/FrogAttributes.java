package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class FrogAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                BuiltInRegistries.FROG_VARIANT.keySet().stream().map(ResourceLocation::getPath).toList(),
                List.of(EntityType.FROG),
                FrogAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Frog frog = ReflectionHelper.getEntity(npc);
        BuiltInRegistries.FROG_VARIANT.getHolder(ResourceLocation.parse(value.toLowerCase()))
                .ifPresent(frog::setVariant);
    }

}
