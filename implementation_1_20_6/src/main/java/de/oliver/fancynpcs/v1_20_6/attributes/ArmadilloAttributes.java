package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ArmadilloAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("idle", "rolling", "unrolling", "scared"),
                List.of(EntityType.ARMADILLO),
                ArmadilloAttributes::setPose
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Armadillo armadillo = ReflectionHelper.getEntity(npc);

        Armadillo.ArmadilloState state = Armadillo.ArmadilloState.valueOf(value.toUpperCase());

        armadillo.switchToState(state);
    }

}
