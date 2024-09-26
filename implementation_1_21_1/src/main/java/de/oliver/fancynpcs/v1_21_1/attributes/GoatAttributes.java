package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.animal.goat.Goat;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class GoatAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "horns",
                List.of("none", "left", "right", "both"),
                List.of(EntityType.GOAT),
                GoatAttributes::setHorns
        ));

        return attributes;
    }

    private static void setHorns(Npc npc, String value) {
        Goat goat = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "none" -> goat.removeHorns();
            case "both" -> goat.addHorns();
            case "left" -> {
                goat.getEntityData().set(Goat.DATA_HAS_LEFT_HORN, true);
                goat.getEntityData().set(Goat.DATA_HAS_RIGHT_HORN, false);
            }
            case "right" -> {
                goat.getEntityData().set(Goat.DATA_HAS_RIGHT_HORN, true);
                goat.getEntityData().set(Goat.DATA_HAS_LEFT_HORN, false);
            }
        }
    }

}
