package de.oliver.fancynpcs.v1_20_5.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_5.ReflectionHelper;
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

        attributes.add(new NpcAttribute(
                "invisible",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setInvisible
        ));


        return attributes;
    }

    private static void setOnFire(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean onFire = Boolean.parseBoolean(value);

        entity.setSharedFlagOnFire(onFire);

    }

    private static void setInvisible(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean invisible = Boolean.parseBoolean(value);

        entity.setInvisible(invisible);
    }
}
