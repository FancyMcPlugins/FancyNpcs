package de.oliver.fancynpcs.v1_20_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_1.ReflectionHelper;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "on_fire",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setOnFire
        ));


        return attributes;
    }

    private static void setOnFire(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean onFire = Boolean.parseBoolean(value);

        entity.setSharedFlagOnFire(onFire);

    }
}
