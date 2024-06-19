package de.oliver.fancynpcs.v1_21;

public enum MappingKeys1_21 {

    ENTITY_TYPE__FACTORY("factory"),
    SYNCHED_ENTITY_DATA__ITEMS_BY_ID("itemsById"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__X("x"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Y("y"),
    CLIENTBOUND_TELEPORT_ENTITY_PACKET__Z("z"),
    PANDA__DATA_ID_FLAGS("DATA_ID_FLAGS"),
    ;

    private final String mapping;

    MappingKeys1_21(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }
}
