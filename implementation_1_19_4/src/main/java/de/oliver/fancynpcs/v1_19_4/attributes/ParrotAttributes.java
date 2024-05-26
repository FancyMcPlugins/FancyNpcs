package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_19_4.ReflectionHelper;
import net.minecraft.world.entity.animal.Parrot;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParrotAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Parrot.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.PARROT),
                ParrotAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting"),
                List.of(EntityType.PARROT),
                ParrotAttributes::setPose
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Parrot parrot = ReflectionHelper.getEntity(npc);

        Parrot.Variant variant = Parrot.Variant.valueOf(value.toUpperCase());
        parrot.setVariant(variant);
    }

    private static void setPose(Npc npc, String value) {
        Parrot parrot = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> {
                parrot.setOrderedToSit(false);
                parrot.setInSittingPose(false, false);
            }
            case "sitting" -> {
                parrot.setOrderedToSit(true);
                parrot.setInSittingPose(true, false);
            }
        }
    }

}
