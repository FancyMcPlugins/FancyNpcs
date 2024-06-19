package de.oliver.fancynpcs.v1_20_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_4.ReflectionHelper;
import net.minecraft.world.InteractionHand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LivingEntityAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        /*attributes.add(new NpcAttribute(
                "hurt",
                List.of("true", "false"),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && LivingEntity.class.isAssignableFrom(type.getEntityClass()))
                        .toList(),
                LivingEntityAttributes::setHurt
        ));*/

        attributes.add(new NpcAttribute(
                "use_item",
                List.of("main_hand", "off_hand", "none"),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && LivingEntity.class.isAssignableFrom(type.getEntityClass()))
                        .toList(),
                LivingEntityAttributes::setUseItem
        ));

        return attributes;
    }

    private static void setHurt(Npc npc, String value) {
        net.minecraft.world.entity.LivingEntity livingEntity = ReflectionHelper.getEntity(npc);

        boolean isHurt = Boolean.parseBoolean(value);

        if (isHurt) {
            livingEntity.hurtDuration = 1;
            livingEntity.hurtTime = 1;
            livingEntity.hurtMarked = true;
            livingEntity.animateHurt(0);
        } else {
            livingEntity.hurtDuration = 0;
            livingEntity.hurtTime = 0;
            livingEntity.hurtMarked = false;
        }
    }

    private static void setUseItem(Npc npc, String value) {
        net.minecraft.world.entity.LivingEntity livingEntity = ReflectionHelper.getEntity(npc);

        switch (value.toUpperCase()) {
            case "NONE" -> livingEntity.stopUsingItem();
            case "MAIN_HAND" -> livingEntity.startUsingItem(InteractionHand.MAIN_HAND, true);
            case "OFF_HAND" -> livingEntity.startUsingItem(InteractionHand.OFF_HAND, true);
        }
    }

}
