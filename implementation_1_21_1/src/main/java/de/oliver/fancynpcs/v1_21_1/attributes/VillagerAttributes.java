package de.oliver.fancynpcs.v1_21_1.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_1.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class VillagerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "profession",
                BuiltInRegistries.VILLAGER_PROFESSION.keySet().stream().map(ResourceLocation::getPath).toList(),
                List.of(EntityType.VILLAGER),
                VillagerAttributes::setProfession
        ));

        attributes.add(new NpcAttribute(
                "type",
                BuiltInRegistries.VILLAGER_TYPE.keySet().stream().map(ResourceLocation::getPath).toList(),
                List.of(EntityType.VILLAGER),
                VillagerAttributes::setType
        ));

        return attributes;
    }

    private static void setProfession(Npc npc, String value) {
        Villager villager = ReflectionHelper.getEntity(npc);

        VillagerProfession profession = BuiltInRegistries.VILLAGER_PROFESSION.get(ResourceLocation.tryParse(value));

        villager.setVillagerData(villager.getVillagerData().setProfession(profession));
    }

    private static void setType(Npc npc, String value) {
        Villager villager = ReflectionHelper.getEntity(npc);

        VillagerType type = BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.tryParse(value));

        villager.setVillagerData(villager.getVillagerData().setType(type));
    }

}
