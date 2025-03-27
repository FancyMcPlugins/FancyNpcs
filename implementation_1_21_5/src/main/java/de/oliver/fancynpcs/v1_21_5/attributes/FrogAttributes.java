package de.oliver.fancynpcs.v1_21_5.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_5.ReflectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class FrogAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getFrogVariantRegistry()
                        .listElementIds()
                        .map(id -> id.location().getPath())
                        .toList(),
                List.of(EntityType.FROG),
                FrogAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Frog frog = ReflectionHelper.getEntity(npc);

        Holder<FrogVariant> variant = getFrogVariantRegistry()
                .get(ResourceKey.create(
                        Registries.FROG_VARIANT,
                        ResourceLocation.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        frog.setVariant(variant);
    }

    private static HolderLookup.RegistryLookup<FrogVariant> getFrogVariantRegistry() {
        return VanillaRegistries
                .createLookup()
                .lookup(Registries.FROG_VARIANT)
                .orElseThrow();
    }
}
