package de.oliver.fancynpcs.skins.cache;

import java.util.List;

public interface SkinCache {

    List<SkinCacheData> load();

    void upsert(SkinCacheData skinData, boolean onlyIfExists);

}
