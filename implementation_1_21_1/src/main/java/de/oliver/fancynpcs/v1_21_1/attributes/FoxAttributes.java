package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.animal.Fox;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoxAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "type",
                Arrays.stream(Fox.Type.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.FOX),
                FoxAttributes::setType
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sleeping", "sitting"),
                List.of(EntityType.FOX),
                FoxAttributes::setPose
        ));

        return attributes;
    }

    private static void setType(Npc npc, String value) {
        Fox fox = ReflectionHelper.getEntity(npc);

        Fox.Type type = Fox.Type.valueOf(value.toUpperCase());
        fox.setVariant(type);
    }

    private static void setPose(Npc npc, String value) {
        Fox fox = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> {
                fox.setIsCrouching(false);
                fox.setSleeping(false);
                fox.setSitting(false, false);
            }
            case "sleeping" -> {
                fox.setSleeping(true);
                fox.setSitting(false, false);
                fox.setIsCrouching(false);
            }
            case "sitting" -> {
                fox.setSitting(true, false);
                fox.setSleeping(false);
                fox.setIsCrouching(false);
            }
        }
    }

}
