package de.oliver.fancynpcs.api.utils;

public enum NpcEquipmentSlot {
    MAINHAND,
    OFFHAND,
    FEET,
    LEGS,
    CHEST,
    HEAD;

    public static NpcEquipmentSlot parse(String s) {
        for (NpcEquipmentSlot slot : values()) {
            if (slot.name().equalsIgnoreCase(s)) {
                return slot;
            }
        }

        return null;
    }

    public String toNmsName() {
        return name().toLowerCase();
    }

}
