package de.oliver.fancynpcs.v1_20_5.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_5.ReflectionHelper;
import net.minecraft.world.entity.animal.camel.Camel;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CamelAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting"),
                List.of(EntityType.CAMEL),
                CamelAttributes::setPose
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Camel camel = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> camel.standUp();
            case "sitting" -> camel.sitDown();
        }
    }

}
