package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AxolotlAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Axolotl.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.AXOLOTL),
                AxolotlAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "playing_dead",
                List.of("true", "false"),
                List.of(EntityType.AXOLOTL),
                AxolotlAttributes::setPlayingDead
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Axolotl axolotl = ReflectionHelper.getEntity(npc);

        Axolotl.Variant variant = Axolotl.Variant.valueOf(value.toUpperCase());
        axolotl.setVariant(variant);
    }

    private static void setPlayingDead(Npc npc, String value) {
        Axolotl axolotl = ReflectionHelper.getEntity(npc);

        boolean playingDead = Boolean.parseBoolean(value);
        axolotl.setPlayingDead(playingDead);
    }

}
