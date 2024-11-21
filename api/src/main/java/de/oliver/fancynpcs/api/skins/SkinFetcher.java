package de.oliver.fancynpcs.api.skins;

import java.util.UUID;

public interface SkinFetcher {
    
    SkinData getByUUID(UUID uuid);

    SkinData getByUsername(String username);

    SkinData getByURL(String url);

    SkinData getByFile(String filePath);

    SkinData get(String name, String value, String signature);

}
