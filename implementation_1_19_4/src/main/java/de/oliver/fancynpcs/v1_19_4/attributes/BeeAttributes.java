package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_19_4.ReflectionHelper;
import net.minecraft.world.entity.animal.Bee;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class BeeAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "angry",
                List.of("true", "false"),
                List.of(EntityType.BEE),
                BeeAttributes::setAngry
        ));

        attributes.add(new NpcAttribute(
                "sting",
                List.of("true", "false"),
                List.of(EntityType.BEE),
                BeeAttributes::setSting
        ));

        attributes.add(new NpcAttribute(
                "nectar",
                List.of("true", "false"),
                List.of(EntityType.BEE),
                BeeAttributes::setNectar
        ));

        attributes.add(new NpcAttribute(
                "rolling",
                List.of("true", "false"),
                List.of(EntityType.BEE),
                BeeAttributes::setRolling
        ));

        return attributes;
    }

    private static void setAngry(Npc npc, String value) {
        Bee bee = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "true" -> bee.setRemainingPersistentAngerTime(1);
            case "false" -> bee.setRemainingPersistentAngerTime(0);
        }
    }

    private static void setSting(Npc npc, String value) {
        Bee bee = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "true" -> bee.setHasStung(false);
            case "false" -> bee.setHasStung(true);
        }
    }

    private static void setNectar(Npc npc, String value) {
        Bee bee = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "true" -> bee.setHasNectar(true);
            case "false" -> bee.setHasNectar(false);
        }
    }

    private static void setRolling(Npc npc, String value) {
        Bee bee = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "true" -> bee.setRolling(true);
            case "false" -> bee.setRolling(false);
        }
    }

}
