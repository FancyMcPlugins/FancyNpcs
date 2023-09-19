package de.oliver.fancynpcs.v1_20_2.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_2.ReflectionHelper;
import net.minecraft.world.entity.animal.Panda;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PandaAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "gene",
                Arrays.stream(Panda.Gene.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.PANDA),
                PandaAttributes::setGene
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting", "onBack"),
                List.of(EntityType.PANDA),
                PandaAttributes::setPose
        ));

        return attributes;
    }

    private static void setGene(Npc npc, String value) {
        Panda panda = ReflectionHelper.getEntity(npc);

        Panda.Gene gene = Panda.Gene.valueOf(value.toUpperCase());
        panda.setMainGene(gene);
    }

    private static void setPose(Npc npc, String value) {
        Panda panda = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> {
                panda.sit(false);
                panda.setOnBack(false);
            }
            case "sitting" -> {
                panda.sit(true);
                panda.setOnBack(false);
            }
            case "onback" -> {
                panda.setOnBack(true);
                panda.sit(false);
            }
        }
    }

}
