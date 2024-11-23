package de.oliver.fancynpcs.skins.cache;


import java.util.ArrayList;
import java.util.List;

public class SkinCacheFake implements SkinCache {

    @Override
    public List<SkinCacheData> load() {
        return new ArrayList<>();
    }

    @Override
    public void upsert(SkinCacheData skinData, boolean onlyIfExists) {

    }
}
