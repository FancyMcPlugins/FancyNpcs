package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VillagerAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "profession",
                Arrays.stream(org.bukkit.entity.Villager.Profession.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.VILLAGER),
                VillagerAttributes::setProfession
        ));

        attributes.add(new NpcAttribute(
                "type",
                Arrays.stream(org.bukkit.entity.Villager.Type.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.VILLAGER),
                VillagerAttributes::setType
        ));

        return attributes;
    }

    private static void setProfession(Npc npc, String value) {
        Villager villager = ReflectionHelper.getEntity(npc);

        VillagerProfession profession;
        switch (value.toUpperCase()) {
            case "ARMORER" -> profession = VillagerProfession.ARMORER;
            case "BUTCHER" -> profession = VillagerProfession.BUTCHER;
            case "CARTOGRAPHER" -> profession = VillagerProfession.CARTOGRAPHER;
            case "CLERIC" -> profession = VillagerProfession.CLERIC;
            case "FARMER" -> profession = VillagerProfession.FARMER;
            case "FISHERMAN" -> profession = VillagerProfession.FISHERMAN;
            case "FLETCHER" -> profession = VillagerProfession.FLETCHER;
            case "LEATHERWORKER" -> profession = VillagerProfession.LEATHERWORKER;
            case "LIBRARIAN" -> profession = VillagerProfession.LIBRARIAN;
            case "MASON" -> profession = VillagerProfession.MASON;
            case "NITWIT" -> profession = VillagerProfession.NITWIT;
            case "SHEPHERD" -> profession = VillagerProfession.SHEPHERD;
            case "TOOLSMITH" -> profession = VillagerProfession.TOOLSMITH;
            case "WEAPONSMITH" -> profession = VillagerProfession.WEAPONSMITH;

            default -> profession = VillagerProfession.NONE;
        }

        villager.setVillagerData(villager.getVillagerData().setProfession(profession));
    }

    private static void setType(Npc npc, String value) {
        Villager villager = ReflectionHelper.getEntity(npc);

        VillagerType type;
        switch (value.toUpperCase()) {
            case "DESERT" -> type = VillagerType.DESERT;
            case "JUNGLE" -> type = VillagerType.JUNGLE;
            case "SAVANNA" -> type = VillagerType.SAVANNA;
            case "SNOW" -> type = VillagerType.SNOW;
            case "SWAMP" -> type = VillagerType.SWAMP;
            case "TAIGA" -> type = VillagerType.TAIGA;

            default -> type = VillagerType.PLAINS;
        }


        villager.setVillagerData(villager.getVillagerData().setType(type));
    }

}
