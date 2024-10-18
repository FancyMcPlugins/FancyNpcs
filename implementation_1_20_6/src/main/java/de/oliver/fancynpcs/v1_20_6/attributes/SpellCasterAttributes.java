package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spellcaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellCasterAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "casting",
                Arrays.stream(SpellcasterIllager.IllagerSpell.values()).map(Enum::toString).toList(),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && Spellcaster.class.isAssignableFrom(type.getEntityClass()))
                        .toList(),
                SpellCasterAttributes::setPose
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        SpellcasterIllager spellcasterIllager = ReflectionHelper.getEntity(npc);

        SpellcasterIllager.IllagerSpell spell = SpellcasterIllager.IllagerSpell.valueOf(value);

        spellcasterIllager.setIsCastingSpell(spell);
    }

}
