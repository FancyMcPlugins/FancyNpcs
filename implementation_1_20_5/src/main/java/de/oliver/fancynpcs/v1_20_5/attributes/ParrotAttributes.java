package de.oliver.fancynpcs.v1_20_5.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_5.ReflectionHelper;
import net.minecraft.world.entity.animal.Parrot;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParrotAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Parrot.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.PARROT),
                ParrotAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Parrot parrot = ReflectionHelper.getEntity(npc);

        Parrot.Variant variant = Parrot.Variant.valueOf(value.toUpperCase());
        parrot.setVariant(variant);
    }

}
