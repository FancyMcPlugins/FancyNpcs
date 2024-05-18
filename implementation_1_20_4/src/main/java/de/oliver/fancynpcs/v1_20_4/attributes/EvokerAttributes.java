package de.oliver.fancynpcs.v1_20_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_4.ReflectionHelper;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class EvokerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("casting", "standing"),
                List.of(EntityType.EVOKER),
                EvokerAttributes::setPose
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Evoker evoker = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> evoker.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            case "casting" -> evoker.setIsCastingSpell(SpellcasterIllager.IllagerSpell.FANGS);
        }
    }

}
