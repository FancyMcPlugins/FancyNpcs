package de.oliver.fancynpcs.v1_20_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_4.ReflectionHelper;
import net.minecraft.world.entity.monster.Vex;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class VexAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "charging",
                List.of("true", "false"),
                List.of(EntityType.VEX),
                VexAttributes::setCharging
        ));

        return attributes;
    }

    private static void setCharging(Npc npc, String value) {
        Vex vex = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "true" -> vex.setIsCharging(true);
            case "false" -> vex.setIsCharging(false);
        }
    }

}
