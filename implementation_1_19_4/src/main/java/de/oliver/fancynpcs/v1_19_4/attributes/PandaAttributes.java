package de.oliver.fancynpcs.v1_19_4.attributes;

import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_19_4.MappingKeys1_19_4;
import de.oliver.fancynpcs.v1_19_4.ReflectionHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
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
                "eating",
                List.of("true", "false"),
                List.of(EntityType.PANDA),
                PandaAttributes::setEating
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting", "onBack", "rolling"),
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
                setFlag(panda, 8, false); //sitting
                panda.roll(false);
                panda.setOnBack(false);
            }
            case "sitting" -> {
                panda.roll(false);
                panda.setOnBack(false);
                setFlag(panda, 8, true); //sitting
            }
            case "onback" -> {
                setFlag(panda, 8, false); //sitting
                panda.roll(false);
                panda.setOnBack(true);
            }
            case "rolling" -> {
                setFlag(panda, 8, false); //sitting
                panda.setOnBack(false);
                panda.roll(true);
            }
        }
    }

    private static void setEating(Npc npc, String value) {
        Panda panda = ReflectionHelper.getEntity(npc);

        boolean eating = Boolean.parseBoolean(value);

        panda.eat(eating);
    }

    private static void setFlag(Panda panda, int mask, boolean value) {
        EntityDataAccessor<Byte> DATA_ID_FLAGS = (EntityDataAccessor<Byte>) ReflectionUtils.getValue(panda, MappingKeys1_19_4.PANDA__DATA_ID_FLAGS.getMapping());

        byte b0 = panda.getEntityData().get(DATA_ID_FLAGS);

        if (value) {
            panda.getEntityData().set(DATA_ID_FLAGS, (byte) (b0 | mask));
        } else {
            panda.getEntityData().set(DATA_ID_FLAGS, (byte) (b0 & ~mask));
        }

    }

}
