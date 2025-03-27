package de.oliver.fancynpcs.skins.cache;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.Base64;

public class SkinCacheFile implements SkinCache {

    private final JDB storage;

    public SkinCacheFile() {
        this.storage = new JDB("plugins/FancyNpcs/.data");
    }

    @Override
    public SkinCacheData getSkin(String identifier) {
        String b64ID = Base64.getEncoder().encodeToString(identifier.getBytes());

        SkinCacheData skinCacheData = null;
        try {
            skinCacheData = this.storage.get("skins/" + b64ID, SkinCacheData.class);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load skin cache");
            FancyNpcs.getInstance().getFancyLogger().error(e);
        }

        if (skinCacheData == null) {
            return null;
        }

        if (skinCacheData.isExpired()) {
            this.storage.delete("skins/" + b64ID);
            return null;
        }

        return skinCacheData;
    }

    @Override
    public void addSkin(SkinData skin) {
        SkinCacheData skinCacheData = new SkinCacheData(skin, System.currentTimeMillis(), CACHE_TIME);

        try {
            String b64ID = Base64.getEncoder().encodeToString(skin.getIdentifier().getBytes());
            this.storage.set("skins/" + b64ID, skinCacheData);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save skin cache");
            FancyNpcs.getInstance().getFancyLogger().error(e);
        }
    }

    @Override
    public void removeSkin(String identifier) {
        String b64ID = Base64.getEncoder().encodeToString(identifier.getBytes());
        this.storage.delete("skins/" + b64ID);
    }

    @Override
    public void clear() {
        storage.delete("skins");
    }
}
