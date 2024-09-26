package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.Display;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "billboard",
                Arrays.stream(org.bukkit.entity.Display.Billboard.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.TEXT_DISPLAY, EntityType.BLOCK_DISPLAY, EntityType.ITEM_DISPLAY),
                DisplayAttributes::setBillboard
        ));

        return attributes;
    }

    private static void setBillboard(Npc npc, String value) {
        Display display = ReflectionHelper.getEntity(npc);

        Display.BillboardConstraints billboard = Display.BillboardConstraints.valueOf(value.toUpperCase());
        display.setBillboardConstraints(billboard);
    }

}
