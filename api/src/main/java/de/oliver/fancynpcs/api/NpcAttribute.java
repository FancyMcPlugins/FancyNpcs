package de.oliver.fancynpcs.api;

import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.function.BiConsumer;

public class NpcAttribute {

    private final String name;
    private final List<String> possibleValues;
    private final List<EntityType> types;
    private final BiConsumer<Npc, String> applyFunc; // npc, value

    public NpcAttribute(String name, List<String> possibleValues, List<EntityType> types, BiConsumer<Npc, String> applyFunc) {
        this.name = name;
        this.possibleValues = possibleValues;
        this.types = types;
        this.applyFunc = applyFunc;
    }

    public boolean isValidValue(String value) {
        if (possibleValues.isEmpty()) {
            return true;
        }

        for (String pv : possibleValues) {
            if (pv.equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    public void apply(Npc npc, String value) {
        applyFunc.accept(npc, value);
    }

    public String getName() {
        return name;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public List<EntityType> getTypes() {
        return types;
    }

}
