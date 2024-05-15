package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class PlayerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "crouching", "sleeping", "swimming", "sitting"),
                List.of(EntityType.PLAYER),
                PlayerAttributes::setPose
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Player player = ReflectionHelper.getEntity(npc);

        Pose pose = Pose.valueOf(value.toUpperCase());

        EntityDataAccessor<Pose> DATA_POSE = (EntityDataAccessor<Pose>) ReflectionUtils.getStaticValue(Entity.class, "DATA_POSE"); // DATA_POSE
        player.getEntityData().set(DATA_POSE, pose);
    }

}
