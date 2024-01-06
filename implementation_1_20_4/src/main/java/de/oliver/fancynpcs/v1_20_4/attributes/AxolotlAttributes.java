package de.oliver.fancynpcs.v1_20_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_4.ReflectionHelper;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AxolotlAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Axolotl.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.AXOLOTL),
                AxolotlAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Axolotl axolotl = ReflectionHelper.getEntity(npc);

        Axolotl.Variant variant = Axolotl.Variant.valueOf(value.toUpperCase());
        axolotl.setVariant(variant);
    }

}
