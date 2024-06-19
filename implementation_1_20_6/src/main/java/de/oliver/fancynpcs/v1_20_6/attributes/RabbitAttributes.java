package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.animal.Rabbit;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RabbitAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Rabbit.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.RABBIT),
                RabbitAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Rabbit rabbit = ReflectionHelper.getEntity(npc);

        Rabbit.Variant variant = Rabbit.Variant.valueOf(value.toUpperCase());
        rabbit.setVariant(variant);
    }

}
