package de.oliver.fancynpcs.skins;

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
}
