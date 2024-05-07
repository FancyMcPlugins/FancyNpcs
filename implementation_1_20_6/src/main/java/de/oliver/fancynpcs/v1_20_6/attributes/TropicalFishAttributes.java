package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TropicalFishAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pattern",
                Arrays.stream(TropicalFish.Pattern.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.TROPICAL_FISH),
                TropicalFishAttributes::setPattern
        ));

        attributes.add(new NpcAttribute(
                "base_color",
                Arrays.stream(DyeColor.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.TROPICAL_FISH),
                TropicalFishAttributes::setBaseColor
        ));

        attributes.add(new NpcAttribute(
                "pattern_color",
                Arrays.stream(DyeColor.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.TROPICAL_FISH),
                TropicalFishAttributes::setPatternColor
        ));

        return attributes;
    }

    private static void setPattern(Npc npc, String value) {
        TropicalFish tropicalFish = ReflectionHelper.getEntity(npc);

        TropicalFish.Pattern pattern = TropicalFish.Pattern.valueOf(value.toUpperCase());
        tropicalFish.setVariant(pattern);
    }

    private static void setBaseColor(Npc npc, String value) {
        TropicalFish tropicalFish = ReflectionHelper.getEntity(npc);

        DyeColor color = DyeColor.byName(value.toLowerCase(), DyeColor.WHITE);
        TropicalFish.Variant variant = new TropicalFish.Variant(tropicalFish.getVariant(), color, tropicalFish.getPatternColor());
        tropicalFish.setPackedVariant(variant.getPackedId());
    }

    private static void setPatternColor(Npc npc, String value) {
        TropicalFish tropicalFish = ReflectionHelper.getEntity(npc);

        DyeColor color = DyeColor.byName(value.toLowerCase(), DyeColor.WHITE);
        TropicalFish.Variant variant = new TropicalFish.Variant(tropicalFish.getVariant(), tropicalFish.getBaseColor(), color);
        tropicalFish.setPackedVariant(variant.getPackedId());
    }

}
