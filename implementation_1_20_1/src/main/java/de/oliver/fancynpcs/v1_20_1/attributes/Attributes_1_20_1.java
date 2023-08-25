package de.oliver.fancynpcs.v1_20_1.attributes;

import de.oliver.fancynpcs.api.NpcAttribute;

import java.util.ArrayList;
import java.util.List;

public class Attributes_1_20_1 {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.addAll(EntityAttributes.getAllAttributes());
        attributes.addAll(AgeableMobAttributes.getAllAttributes());

        attributes.addAll(PlayerAttributes.getAllAttributes());
        attributes.addAll(SheepAttributes.getAllAttributes());
        attributes.addAll(VillagerAttributes.getAllAttributes());
        attributes.addAll(FrogAttributes.getAllAttributes());
        attributes.addAll(HorseAttributes.getAllAttributes());
        attributes.addAll(ParrotAttributes.getAllAttributes());
        attributes.addAll(AxolotlAttributes.getAllAttributes());
        attributes.addAll(TropicalFishAttributes.getAllAttributes());
        attributes.addAll(FoxAttributes.getAllAttributes());

        attributes.addAll(DisplayAttributes.getAllAttributes());
        attributes.addAll(TextDisplayAttributes.getAllAttributes());
        attributes.addAll(BlockDisplayAttributes.getAllAttributes());

        return attributes;
    }

}
