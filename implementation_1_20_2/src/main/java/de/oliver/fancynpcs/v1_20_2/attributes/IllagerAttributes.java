package de.oliver.fancynpcs.v1_20_2.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_2.ReflectionHelper;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IllagerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "celebrating",
                List.of("true", "false"),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && Illager.class.isAssignableFrom(type.getEntityClass()))
                        .toList(),
                IllagerAttributes::setCelebrating
        ));

        return attributes;
    }

    private static void setCelebrating(Npc npc, String value) {
        Raider raider = ReflectionHelper.getEntity(npc);

        boolean isCelebrating = Boolean.parseBoolean(value);

        raider.setCelebrating(isCelebrating);
    }

}
