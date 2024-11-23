package de.oliver.fancynpcs.skins;

import de.oliver.fancynpcs.api.skins.SkinData;

public class SkinUtils {

    public static boolean isPlaceholder(String identifier) {
        return identifier.startsWith("%") && identifier.endsWith("%") || identifier.startsWith("{") && identifier.endsWith("}");
    }

    public static boolean isUUID(String identifier) {
        return identifier.length() == 36 && identifier.contains("-");
    }

    public static boolean isURL(String identifier) {
        return identifier.startsWith("http");
    }

    public static boolean isFile(String identifier) {
        return identifier.endsWith(".png") || identifier.endsWith(".jpg") || identifier.endsWith(".jpeg");
    }

    public static SkinData.SkinType getSkinType(String identifier) {
        if (isPlaceholder(identifier)) {
            return SkinData.SkinType.PLACEHOLDER;
        }

        if (isUUID(identifier)) {
            return SkinData.SkinType.UUID;
        }

        if (isURL(identifier)) {
            return SkinData.SkinType.URL;
        }

        if (isFile(identifier)) {
            return SkinData.SkinType.FILE;
        }

        return SkinData.SkinType.USERNAME;
    }

}
