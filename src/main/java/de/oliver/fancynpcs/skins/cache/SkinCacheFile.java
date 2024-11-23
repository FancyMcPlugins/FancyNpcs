package de.oliver.fancynpcs.skins.cache;

import de.oliver.fancylib.jdb.JDB;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;

import java.io.IOException;

public class SkinCacheFile implements SkinCache {

    private final JDB storage;

    public SkinCacheFile() {
        this.storage = new JDB("plugins/FancyNpcs/.cache");
    }

    @Override
    public SkinCacheData getSkin(String identifier) {
        SkinCacheData skinCacheData = null;
        try {
            skinCacheData = this.storage.get("skins/" + identifier, SkinCacheData.class);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load skin cache");
            FancyNpcs.getInstance().getFancyLogger().error(e);
        }

        if (skinCacheData == null) {
            return null;
        }

        if (skinCacheData.isExpired()) {
            this.storage.delete("skins/" + identifier);
            return null;
        }

        return skinCacheData;
    }

    @Override
    public void addSkin(SkinData skin) {
        SkinCacheData skinCacheData = new SkinCacheData(skin, System.currentTimeMillis(), CACHE_TIME);

        try {
            this.storage.set("skins/" + skin.getIdentifier(), skinCacheData);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save skin cache");
            FancyNpcs.getInstance().getFancyLogger().error(e);
        }
    }

    @Override
    public void removeSkin(String identifier) {
        this.storage.delete("skins/" + identifier);
    }
}
