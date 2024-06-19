package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.Interaction;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class InteractionAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "height",
                new ArrayList<>(),
                List.of(EntityType.INTERACTION),
                InteractionAttributes::setHeight
        ));

        attributes.add(new NpcAttribute(
                "width",
                new ArrayList<>(),
                List.of(EntityType.INTERACTION),
                InteractionAttributes::setWidth
        ));

        return attributes;
    }

    private static void setHeight(Npc npc, String value) {
        Interaction interaction = ReflectionHelper.getEntity(npc);

        float height;
        try {
            height = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return;
        }

        interaction.setHeight(height);
    }

    private static void setWidth(Npc npc, String value) {
        Interaction interaction = ReflectionHelper.getEntity(npc);

        float width;
        try {
            width = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return;
        }

        interaction.setWidth(width);
    }

}
