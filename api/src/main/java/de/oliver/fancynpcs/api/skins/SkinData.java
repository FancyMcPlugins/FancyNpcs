package de.oliver.fancynpcs.api.skins;

public record SkinData(
        String identifier,
        SkinType type,
        SkinVariant variant,

        String textureValue,
        String textureSignature
) {

    public enum SkinVariant {
        SLIM,
        DEFAULT
    }

    public enum SkinType {
        USERNAME,
        UUID,
        URL,
        FILE,
        VALUE_SIGNATURE
    }
}
