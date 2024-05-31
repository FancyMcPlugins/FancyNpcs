package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "on_fire",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setOnFire
        ));

        attributes.add(new NpcAttribute(
                "invisible",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setInvisible
        ));

        attributes.add(new NpcAttribute(
                "silent",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setSilent
        ));

        attributes.add(new NpcAttribute(
                "shaking",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setShaking
        ));

        attributes.add(new NpcAttribute(
                "on_ground",
                List.of("true", "false"),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setOnGround
        ));

        /*attributes.add(new NpcAttribute(
                "entity_pose",
                Arrays.stream(Pose.values()).map(Enum::toString).toList(),
                Arrays.stream(EntityType.values()).toList(),
                EntityAttributes::setEntityPose
        ));*/

        return attributes;
    }

    private static void setOnFire(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean onFire = Boolean.parseBoolean(value);

        entity.setSharedFlagOnFire(onFire);

    }

    private static void setInvisible(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean invisible = Boolean.parseBoolean(value);

        entity.setInvisible(invisible);
    }

    private static void setSilent(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean silent = Boolean.parseBoolean(value);

        entity.setSilent(silent);
    }

    private static void setShaking(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean shaking = Boolean.parseBoolean(value);

        entity.setTicksFrozen(shaking ? entity.getTicksRequiredToFreeze() : 0);
    }

    private static void setOnGround(Npc npc, String value) {
        Entity entity = ReflectionHelper.getEntity(npc);

        boolean onGround = Boolean.parseBoolean(value);

        entity.setOnGround(onGround);
    }
}
