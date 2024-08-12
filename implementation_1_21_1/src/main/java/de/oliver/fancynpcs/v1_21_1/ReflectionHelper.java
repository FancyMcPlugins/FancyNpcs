package de.oliver.fancynpcs.v1_21_1;

import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import net.minecraft.world.entity.Entity;

public class ReflectionHelper {

    public static <T extends Entity> T getEntity(Npc npc) {
        return (T) ReflectionUtils.getValue(npc, "npc");
    }

}
