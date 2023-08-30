package de.oliver.fancynpcs.v1_20_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_1.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(org.bukkit.entity.Cat.Type.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.CAT),
                CatAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sleeping"),
                List.of(EntityType.CAT),
                CatAttributes::setPose
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Cat cat = ReflectionHelper.getEntity(npc);

        CatVariant variant = BuiltInRegistries.CAT_VARIANT.get(ResourceLocation.of("minecraft:" + value.toLowerCase(), ':'));
        if (variant == null) return;

        cat.setVariant(variant);
    }

    private static void setPose(Npc npc, String value) {
        Cat cat = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> cat.setLying(false);
            case "sleeping" -> cat.setLying(true);
        }
    }

}
