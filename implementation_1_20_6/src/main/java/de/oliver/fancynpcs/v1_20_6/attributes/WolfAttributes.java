package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class WolfAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting"),
                List.of(EntityType.WOLF),
                WolfAttributes::setPose
        ));

        attributes.add(new NpcAttribute(
                "angry",
                List.of("true", "false"),
                List.of(EntityType.WOLF),
                WolfAttributes::setAngry
        ));

        attributes.add(new NpcAttribute(
                "variant",
                List.of("PALE", "SPOTTED", "SNOWY", "BLACK", "ASHEN", "RUSTY", "WOODS", "CHESTNUT", "STRIPED"),
                List.of(EntityType.WOLF),
                WolfAttributes::setVariant
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> wolf.setInSittingPose(false, false);
            case "sitting" -> wolf.setInSittingPose(true, false);
        }
    }

    private static void setAngry(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        boolean angry = Boolean.parseBoolean(value.toLowerCase());

        wolf.setRemainingPersistentAngerTime(angry ? 100 : 0);
    }

    private static void setVariant(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        Registry<WolfVariant> registry = wolf.level().registryAccess().registry(Registries.WOLF_VARIANT).get();
        WolfVariant variant = registry.get(ResourceLocation.of(value.toLowerCase(), ':'));

        if (variant != null) {
            wolf.setVariant(Holder.direct(variant));
        }
    }
}
