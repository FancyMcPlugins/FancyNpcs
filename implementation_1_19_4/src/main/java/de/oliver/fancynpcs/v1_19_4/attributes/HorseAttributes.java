package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_19_4.ReflectionHelper;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HorseAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.HORSE),
                HorseAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "markings",
                Arrays.stream(Markings.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.HORSE),
                HorseAttributes::setMarkings
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Horse horse = ReflectionHelper.getEntity(npc);

        Variant variant = Variant.valueOf(value.toUpperCase());
        horse.setVariant(variant);
    }

    private static void setMarkings(Npc npc, String value) {
        Horse horse = ReflectionHelper.getEntity(npc);

        Markings markings = Markings.valueOf(value.toUpperCase());
        horse.setVariantAndMarkings(horse.getVariant(), markings);
    }

}
