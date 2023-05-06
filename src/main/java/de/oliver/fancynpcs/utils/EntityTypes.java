package de.oliver.fancynpcs.utils;

import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityTypes {

    public static Map<String, EntityType<?>> TYPES = new HashMap<>();

    public static void loadTypes(){
        TYPES.clear();

        for (Field field : net.minecraft.world.entity.EntityType.class.getFields()) {
            try{
                field.setAccessible(true);
                Object possibleType = field.get(null);
                if(!(possibleType instanceof net.minecraft.world.entity.EntityType<?> type)){
                    continue;
                }

                TYPES.put(type.toShortString(), type);

            } catch (Exception e){ }
        }
    }

}
