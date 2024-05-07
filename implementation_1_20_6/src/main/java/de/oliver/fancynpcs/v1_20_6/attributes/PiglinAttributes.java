package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class PiglinAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "dancing",
                List.of("true", "false"),
                List.of(EntityType.PIGLIN),
                PiglinAttributes::setDancing
        ));

        return attributes;
    }

    private static void setDancing(Npc npc, String value) {
        Piglin piglin = ReflectionHelper.getEntity(npc);

        boolean dancing = Boolean.parseBoolean(value);
        piglin.setDancing(dancing);
    }

}
