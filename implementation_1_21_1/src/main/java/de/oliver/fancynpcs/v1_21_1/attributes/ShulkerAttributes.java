package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ShulkerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "color",
                Arrays.stream(DyeColor.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.SHULKER),
                ShulkerAttributes::setColor
        ));

        attributes.add(new NpcAttribute(
                "shield",
                List.of("open", "closed"),
                List.of(EntityType.SHULKER),
                ShulkerAttributes::setShield
        ));

        return attributes;
    }

    private static void setColor(Npc npc, String value) {
        Shulker shulker = ReflectionHelper.getEntity(npc);

        DyeColor color = DyeColor.byName(value.toLowerCase(), DyeColor.PURPLE);
        shulker.setVariant(Optional.of(color));
    }

    private static void setShield(Npc npc, String value) {
        Shulker shulker = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "closed" -> shulker.setRawPeekAmount(0);
            case "open" -> shulker.setRawPeekAmount(Byte.MAX_VALUE);
        }
    }

}
