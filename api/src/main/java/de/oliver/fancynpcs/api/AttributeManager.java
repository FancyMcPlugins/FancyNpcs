package de.oliver.fancynpcs.api;

import org.bukkit.entity.EntityType;

import java.util.List;

public interface AttributeManager {

    NpcAttribute getAttributeByName(EntityType type, String name);

    List<NpcAttribute> getAllAttributes();

    List<NpcAttribute> getAllAttributesForEntityType(EntityType type);

    void registerAttribute(NpcAttribute attribute);
}
