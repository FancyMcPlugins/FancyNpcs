package de.oliver.fancynpcs.v1_20_1;

public enum MappingKeys1_20_1 {

    ENTITY_TYPE__FACTORY("bA"),
    SYNCHED_ENTITY_DATA__ITEMS_BY_ID("e"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__X("b"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Y("c"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Z("d"),
    CLIENTBOUND_PLAYER_INFO_UPDATE_PACKET__ENTRIES("b"),
    PANDA__DATA_ID_FLAGS("cb"),
    ;

    private final String mapping;

    MappingKeys1_20_1(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
