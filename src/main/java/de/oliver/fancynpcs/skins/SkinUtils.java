package de.oliver.fancynpcs.skins;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
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

    public static void applySkinLater(String npcID, String skinID, SkinData.SkinVariant variant, Runnable successCallback, Runnable errorCallback) {
        MineSkinQueue.get().add(() -> {
            FancyNpcs.getInstance().getFancyLogger().debug("Loading skin for npc '" + npcID + "'. Skin: " + skinID + ", Variant: " + variant);

            SkinData skin = FancyNpcs.getInstance().getSkinManagerImpl().getByIdentifier(skinID, variant);
            if (skin == null) {
                FancyNpcs.getInstance().getFancyLogger().error("Could not fetch skin for npc '" + npcID + "'");
                errorCallback.run();
                return;
            }

            Npc npc = FancyNpcs.getInstance().getNpcManager().getNpcById(npcID);
            if (npc == null) {
                FancyNpcs.getInstance().getFancyLogger().error("Could not find npc '" + npcID + "'");
                errorCallback.run();
                return;
            }

            npc.getData().setMirrorSkin(false);
            npc.getData().setSkin(skin);
            npc.removeForAll();
            npc.create();
            npc.spawnForAll();

            FancyNpcs.getInstance().getFancyLogger().debug("Successfully applied skin for npc '" + npcID + "'");
            successCallback.run();
        });
        System.out.println("Added skin to queue");
    }

    public static void applySkinLater(String npcID, String skinID, SkinData.SkinVariant variant) {
        applySkinLater(npcID, skinID, variant, () -> {
        }, () -> {
        });
    }
}
