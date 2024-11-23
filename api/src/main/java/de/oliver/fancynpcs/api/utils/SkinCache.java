package de.oliver.fancynpcs.api.utils;

import java.util.List;

/**
 * The skin system is deprecated and will be replaced with a slightly different system in the near future.
 */
@Deprecated
public interface SkinCache {

    /**
     * Load all cached skins from the cache and removes all expired skins
     *
     * @return List of cached skins
     */
    @Deprecated
    List<SkinFetcher.SkinCacheData> load();

    /**
     * Save a skin to the cache
     *
     * @param skinCacheData Skin data to save
     * @param onlyIfExists  If true, the skin will only be replaced/updated if it already exists in the cache
     */
    @Deprecated
    void upsert(SkinFetcher.SkinCacheData skinCacheData, boolean onlyIfExists);

    @Deprecated
    default void upsert(SkinFetcher.SkinCacheData skinCacheData) {
        upsert(skinCacheData, false);
    }

}
