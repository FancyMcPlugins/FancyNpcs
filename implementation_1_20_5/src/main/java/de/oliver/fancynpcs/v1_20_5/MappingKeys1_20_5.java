package de.oliver.fancynpcs.v1_20_5;

public enum MappingKeys1_20_5 {

    ENTITY_TYPE__FACTORY("bC"),
    SYNCHED_ENTITY_DATA__ITEMS_BY_ID("e"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__X("b"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Y("c"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Z("d"),
    PANDA__DATA_ID_FLAGS("cb"),
    ;

    private final String mapping;

    MappingKeys1_20_5(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}