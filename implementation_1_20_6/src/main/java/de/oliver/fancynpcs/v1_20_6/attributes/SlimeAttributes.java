package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class SlimeAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "size",
                new ArrayList<>(),
                List.of(EntityType.SLIME),
                SlimeAttributes::setSize
        ));

        return attributes;
    }

    private static void setSize(Npc npc, String value) {
        Slime slime = ReflectionHelper.getEntity(npc);

        int size;
        try {
            size = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return;
        }

        slime.setSize(size, false);
    }

}
