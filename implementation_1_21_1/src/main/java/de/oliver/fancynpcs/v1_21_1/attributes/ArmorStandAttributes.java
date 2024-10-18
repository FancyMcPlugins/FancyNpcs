package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "show_arms",
                List.of("true", "false"),
                List.of(EntityType.ARMOR_STAND),
                ArmorStandAttributes::setShowArms
        ));

        return attributes;
    }

    private static void setShowArms(Npc npc, String value) {
        ArmorStand armorStand = ReflectionHelper.getEntity(npc);

        boolean showArms = Boolean.parseBoolean(value.toLowerCase());

        armorStand.setShowArms(showArms);
    }

}
