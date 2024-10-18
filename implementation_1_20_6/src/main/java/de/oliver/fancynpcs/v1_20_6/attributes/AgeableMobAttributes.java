package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.AgeableMob;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgeableMobAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "baby",
                List.of("true", "false"),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && Ageable.class.isAssignableFrom(type.getEntityClass()))
                        .toList(),
                AgeableMobAttributes::setBaby
        ));

        return attributes;
    }

    private static void setBaby(Npc npc, String value) {
        AgeableMob mob = ReflectionHelper.getEntity(npc);

        boolean isBaby = Boolean.parseBoolean(value);

        mob.setBaby(isBaby);
    }
}
