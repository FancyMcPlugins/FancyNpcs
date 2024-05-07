package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.animal.Pig;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class PigAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "has_saddle",
                List.of("true", "false"),
                List.of(EntityType.PIG),
                PigAttributes::setHasSaddle
        ));

        return attributes;
    }

    private static void setHasSaddle(Npc npc, String value) {
        Pig pig = ReflectionHelper.getEntity(npc);

        boolean hasSaddle = Boolean.parseBoolean(value.toLowerCase());

        pig.steering.setSaddle(hasSaddle);
    }

}
