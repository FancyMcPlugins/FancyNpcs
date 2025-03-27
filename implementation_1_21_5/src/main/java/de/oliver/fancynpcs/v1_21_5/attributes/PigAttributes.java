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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.item.Items;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class PigAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getPigVariantRegistry()
                        .listElementIds()
                        .map(id -> id.location().getPath())
                        .toList(),
                List.of(EntityType.CAT),
                PigAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "has_saddle",
                List.of("true", "false"),
                List.of(EntityType.PIG),
                PigAttributes::setHasSaddle
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Pig pig = ReflectionHelper.getEntity(npc);

        Holder<PigVariant> variant = getPigVariantRegistry()
                .get(ResourceKey.create(
                        Registries.PIG_VARIANT,
                        ResourceLocation.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        pig.setVariant(variant);
    }

    private static void setHasSaddle(Npc npc, String value) {
        Pig pig = ReflectionHelper.getEntity(npc);

        boolean hasSaddle = Boolean.parseBoolean(value.toLowerCase());

        if (hasSaddle) {
            pig.setItemSlot(EquipmentSlot.SADDLE, Items.SADDLE.getDefaultInstance());
        }
    }

    private static HolderLookup.RegistryLookup<PigVariant> getPigVariantRegistry() {
        return VanillaRegistries
                .createLookup()
                .lookup(Registries.PIG_VARIANT)
                .orElseThrow();
    }

}
