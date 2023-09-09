package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancynpcs.api.NpcAttribute;

import java.util.ArrayList;
import java.util.List;

public class Attributes_1_19_4 {

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
        attributes.addAll(PandaAttributes.getAllAttributes());
        attributes.addAll(GoatAttributes.getAllAttributes());
        attributes.addAll(AllayAttributes.getAllAttributes());
        attributes.addAll(CamelAttributes.getAllAttributes());
        attributes.addAll(RabbitAttributes.getAllAttributes());
        attributes.addAll(PiglinAttributes.getAllAttributes());
        attributes.addAll(CatAttributes.getAllAttributes());
        attributes.addAll(ShulkerAttributes.getAllAttributes());
        attributes.addAll(WolfAttributes.getAllAttributes());
        attributes.addAll(SlimeAttributes.getAllAttributes());
        attributes.addAll(PigAttributes.getAllAttributes());

        attributes.addAll(DisplayAttributes.getAllAttributes());
        attributes.addAll(TextDisplayAttributes.getAllAttributes());
        attributes.addAll(BlockDisplayAttributes.getAllAttributes());

        return attributes;
    }

}
