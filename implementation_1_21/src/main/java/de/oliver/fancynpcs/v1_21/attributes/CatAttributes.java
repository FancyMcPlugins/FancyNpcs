package de.oliver.fancynpcs.v1_21.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21.ReflectionHelper;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CatAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT).stream().map(variant -> variant.getKey().getKey()).toList(),
                List.of(EntityType.CAT),
                CatAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sleeping", "sitting"),
                List.of(EntityType.CAT),
                CatAttributes::setPose
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Cat cat = ReflectionHelper.getEntity(npc);
        BuiltInRegistries.CAT_VARIANT.getHolder(ResourceLocation.parse(value.toLowerCase()))
                .ifPresent(cat::setVariant);
    }

    private static void setPose(Npc npc, String value) {
        final Cat cat = ReflectionHelper.getEntity(npc);
        switch (value.toLowerCase()) {
            case "standing" -> {
                cat.setInSittingPose(false, false);
                cat.setLying(false);
            }
            case "sleeping" -> {
                cat.setInSittingPose(false, false);
                cat.setLying(true);
            }
            case "sitting" -> {
                cat.setLying(false);
                cat.setOrderedToSit(true);
                cat.setInSittingPose(true, false);
            }
        }
    }

}
