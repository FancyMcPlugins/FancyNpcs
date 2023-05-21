package de.oliver.fancynpcs.utils;

import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTypes {

    private static final List<EntityType<?>> excludedTypes = new ArrayList<>();
    public static Map<String, EntityType<?>> TYPES = new HashMap<>();

    static {
        excludedTypes.add(EntityType.AREA_EFFECT_CLOUD);
        excludedTypes.add(EntityType.BLOCK_DISPLAY);
        excludedTypes.add(EntityType.ARROW);
        excludedTypes.add(EntityType.EGG);
        excludedTypes.add(EntityType.ENDER_PEARL);
        excludedTypes.add(EntityType.EVOKER_FANGS);
        excludedTypes.add(EntityType.EXPERIENCE_BOTTLE);
        excludedTypes.add(EntityType.EXPERIENCE_ORB);
        excludedTypes.add(EntityType.FALLING_BLOCK);
        excludedTypes.add(EntityType.FIREWORK_ROCKET);
        excludedTypes.add(EntityType.FISHING_BOBBER);
        excludedTypes.add(EntityType.INTERACTION);
        excludedTypes.add(EntityType.ITEM);
        excludedTypes.add(EntityType.ITEM_DISPLAY);
        excludedTypes.add(EntityType.LIGHTNING_BOLT);
        excludedTypes.add(EntityType.LLAMA_SPIT);
        excludedTypes.add(EntityType.MARKER);
        excludedTypes.add(EntityType.PAINTING);
        excludedTypes.add(EntityType.POTION);
        excludedTypes.add(EntityType.SPECTRAL_ARROW);
        excludedTypes.add(EntityType.TEXT_DISPLAY);
        excludedTypes.add(EntityType.TNT);
        excludedTypes.add(EntityType.TRIDENT);
    }

    public static void loadTypes() {
        TYPES.clear();

        for (Field field : net.minecraft.world.entity.EntityType.class.getFields()) {
            try {
                field.setAccessible(true);
                Object possibleType = field.get(null);
                if (!(possibleType instanceof net.minecraft.world.entity.EntityType<?> type)) {
                    continue;
                }

                if (excludedTypes.contains(type)) {
                    continue;
                }

                TYPES.put(type.toShortString(), type);
            } catch (Exception ex) {
            }
        }
    }

}
