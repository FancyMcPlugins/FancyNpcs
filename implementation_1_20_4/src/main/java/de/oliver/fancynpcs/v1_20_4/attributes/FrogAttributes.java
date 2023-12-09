package de.oliver.fancynpcs.v1_20_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_4.ReflectionHelper;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrogAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(org.bukkit.entity.Frog.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.FROG),
                FrogAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Frog frog = ReflectionHelper.getEntity(npc);

        FrogVariant variant;
        switch (value.toUpperCase()) {
            case "COLD" -> variant = FrogVariant.COLD;
            case "WARM" -> variant = FrogVariant.WARM;
            default -> variant = FrogVariant.TEMPERATE;
        }

        frog.setVariant(variant);
    }

}
