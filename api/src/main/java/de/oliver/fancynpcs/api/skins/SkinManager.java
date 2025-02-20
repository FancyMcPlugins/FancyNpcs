package de.oliver.fancynpcs.api.skins;

import java.util.UUID;

public interface SkinManager {

    /**
     * Fetch a skin by its identifier and variant
     *
     * @param identifier either a valid UUID, username, URL or file path
     * @return the skin data, if the skin was cached. Otherwise, null is returned and the skin is fetched asynchronously. You can listen to the {@link SkinGeneratedEvent} to get the skin data
     */
    SkinData getByIdentifier(String identifier, SkinData.SkinVariant variant);

    /**
     * Fetch a skin by a UUID of a player
     *
     * @return the skin data, if the skin was cached. Otherwise, null is returned and the skin is fetched asynchronously. You can listen to the {@link SkinGeneratedEvent} to get the skin data
     */
    SkinData getByUUID(UUID uuid, SkinData.SkinVariant variant);

    /**
     * Fetch a skin by a username of a player
     *
     * @return the skin data, if the skin was cached. Otherwise, null is returned and the skin is fetched asynchronously. You can listen to the {@link SkinGeneratedEvent} to get the skin data
     */
    SkinData getByUsername(String username, SkinData.SkinVariant variant);

    /**
     * Fetch a skin by a URL pointing to a skin image
     *
     * @return the skin data, if the skin was cached. Otherwise, null is returned and the skin is fetched asynchronously. You can listen to the {@link SkinGeneratedEvent} to get the skin data
     */
    SkinData getByURL(String url, SkinData.SkinVariant variant);

    /**
     * Fetch a skin by a file path pointing to a skin image (relative to plugins/FancyNPCs/skins)
     *
     * @return the skin data, if the skin was cached. Otherwise, null is returned and the skin is fetched asynchronously. You can listen to the {@link SkinGeneratedEvent} to get the skin data
     */
    SkinData getByFile(String filePath, SkinData.SkinVariant variant);

}
