package de.oliver.fancynpcs.skins.cache;

public interface SkinCache {

    SkinCacheData getSkin(String identifier);

    void addSkin(SkinCacheData skin);

    void removeSkin(String identifier);

}
