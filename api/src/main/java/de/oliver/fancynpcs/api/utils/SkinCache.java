package de.oliver.fancynpcs.api.utils;

import java.util.List;

public interface SkinCache {

    /**
     * Load all cached skins from the cache and removes all expired skins
     *
     * @return List of cached skins
     */
    List<SkinFetcher.SkinCacheData> load();

    /**
     * Save a skin to the cache
     *
     * @param skinCacheData Skin data to save
     * @param onlyIfExists  If true, the skin will only be replaced/updated if it already exists in the cache
     */
    void upsert(SkinFetcher.SkinCacheData skinCacheData, boolean onlyIfExists);

    default void upsert(SkinFetcher.SkinCacheData skinCacheData) {
        upsert(skinCacheData, false);
    }

}
