package de.oliver.fancynpcs.api.utils;

import java.util.List;

public interface SkinCache {

    List<SkinFetcher.SkinCacheData> load();

    void upsert(SkinFetcher.SkinCacheData skinCacheData);

}
