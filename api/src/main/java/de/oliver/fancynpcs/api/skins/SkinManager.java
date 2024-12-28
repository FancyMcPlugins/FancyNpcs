package de.oliver.fancynpcs.api.skins;

import java.util.UUID;

public interface SkinManager {

    SkinData getByIdentifier(String identifier, SkinData.SkinVariant variant);

    SkinData getByUUID(UUID uuid, SkinData.SkinVariant variant);

    SkinData getByUsername(String username, SkinData.SkinVariant variant);

    SkinData getByURL(String url, SkinData.SkinVariant variant);

    SkinData getByFile(String filePath, SkinData.SkinVariant variant);

}
