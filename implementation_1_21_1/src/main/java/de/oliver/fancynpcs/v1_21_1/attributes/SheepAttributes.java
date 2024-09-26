package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheepAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "wool_color",
                Arrays.stream(DyeColor.values()).map(dyeColor -> dyeColor.name().toLowerCase()).toList(),
                List.of(EntityType.SHEEP),
                SheepAttributes::setColor
        ));

        attributes.add(new NpcAttribute(
                "sheared",
                Arrays.asList("true", "false"),
                List.of(EntityType.SHEEP),
                SheepAttributes::setSheared
        ));

        return attributes;
    }

    private static void setColor(Npc npc, String value) {
        Sheep sheep = ReflectionHelper.getEntity(npc);

        sheep.setColor(DyeColor.byName(value.toLowerCase(), DyeColor.WHITE));
    }

    private static void setSheared(Npc npc, String value) {
        Sheep sheep = ReflectionHelper.getEntity(npc);

        boolean sheared = Boolean.parseBoolean(value);

        sheep.setSheared(sheared);
    }

}
