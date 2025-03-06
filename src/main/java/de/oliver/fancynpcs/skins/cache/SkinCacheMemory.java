package de.oliver.fancynpcs.skins.cache;


import de.oliver.fancynpcs.api.skins.SkinData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkinCacheMemory implements SkinCache {

    private final Map<String, SkinCacheData> cache;

    public SkinCacheMemory() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public SkinCacheData getSkin(String identifier) {
        if (!cache.containsKey(identifier)) {
            return null;
        }

        SkinCacheData skinCacheData = cache.get(identifier);
        if (skinCacheData.isExpired()) {
            cache.remove(identifier);
            return null;
        }

        return skinCacheData;
    }

    @Override
    public void addSkin(SkinData skin) {
        SkinCacheData skinCacheData = new SkinCacheData(skin, System.currentTimeMillis(), CACHE_TIME);
        cache.put(skin.getIdentifier(), skinCacheData);
    }

    @Override
    public void removeSkin(String identifier) {
        cache.remove(identifier);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
