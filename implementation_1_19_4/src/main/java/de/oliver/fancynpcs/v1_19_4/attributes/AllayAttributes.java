package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_19_4.ReflectionHelper;
import net.minecraft.world.entity.animal.allay.Allay;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class AllayAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "dancing",
                List.of("true", "false"),
                List.of(EntityType.ALLAY),
                AllayAttributes::setDancing
        ));

        return attributes;
    }

    private static void setDancing(Npc npc, String value) {
        Allay allay = ReflectionHelper.getEntity(npc);

        boolean dancing = Boolean.parseBoolean(value);
        allay.setDancing(dancing);
    }

}
