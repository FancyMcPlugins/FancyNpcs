package de.oliver.fancynpcs.skins;

import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.api.skins.SkinManager;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import de.oliver.fancynpcs.skins.cache.SkinCacheData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.UUID;

public class SkinManagerImpl implements SkinManager, Listener {

    private final String SKINS_DIRECTORY = "plugins/FancyNpcs/skins/";

    private final SkinCache fileCache;
    private final SkinCache memCache;

    public SkinManagerImpl(SkinCache fileCache, SkinCache memCache) {
        this.fileCache = fileCache;
        this.memCache = memCache;

        File skinsDir = new File(SKINS_DIRECTORY);
        if (!skinsDir.exists()) {
            skinsDir.mkdirs();
        }
    }

    @Override
    public SkinData getByIdentifier(String identifier, SkinData.SkinVariant variant) {
        if (SkinUtils.isUUID(identifier)) {
            return getByUUID(UUID.fromString(identifier), variant);
        }

        if (SkinUtils.isURL(identifier)) {
            return getByURL(identifier, variant);
        }

        if (SkinUtils.isFile(identifier)) {
            return getByFile(identifier, variant);
        }

        if (SkinUtils.isPlaceholder(identifier)) {
            String parsed = ChatColorHandler.translate(identifier);

            if (SkinUtils.isPlaceholder(parsed)) {
                return null;
            }

            return getByIdentifier(parsed, variant);
        }

        // is username
        UUID uuid = UUIDFetcher.getUUID(identifier);
        if (uuid == null) {
            return null;
        }

        return getByUUID(uuid, variant);
    }

    public SkinData getByIdentifierCached(String identifier, SkinData.SkinVariant variant) {
        if (SkinUtils.isUUID(identifier) || SkinUtils.isURL(identifier) || SkinUtils.isFile(identifier)) {
            return tryToGetFromCache(identifier, variant);
        }

        if (SkinUtils.isPlaceholder(identifier)) {
            String parsed = ChatColorHandler.translate(identifier);

            if (SkinUtils.isPlaceholder(parsed)) {
                return null;
            }

            return tryToGetFromCache(parsed, variant);
        }

        // is username
        UUID uuid = UUIDFetcher.getUUID(identifier);
        if (uuid == null) {
            return null;
        }

        return tryToGetFromCache(uuid.toString(), variant);
    }

    @Override
    public SkinData getByUUID(UUID uuid, SkinData.SkinVariant variant) {
        SkinData cached = tryToGetFromCache(uuid.toString(), variant);
        if (cached != null) {
            return cached;
        }

        GenerateRequest genReq = GenerateRequest.user(uuid);
        genReq.variant(Variant.valueOf(variant.name()));
        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(uuid.toString(), genReq));
        return null;
    }

    @Override
    public SkinData getByUsername(String username, SkinData.SkinVariant variant) {
        UUID uuid = UUIDFetcher.getUUID(username);

        SkinData cached = tryToGetFromCache(uuid.toString(), variant);
        if (cached != null) {
            return cached;
        }

        GenerateRequest genReq = GenerateRequest.user(uuid);
        genReq.variant(Variant.valueOf(variant.name()));
        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(uuid.toString(), genReq));
        return null;
    }

    @Override
    public SkinData getByURL(String url, SkinData.SkinVariant variant) {
        SkinData cached = tryToGetFromCache(url, variant);
        if (cached != null) {
            return cached;
        }

        GenerateRequest genReq;
        try {
            genReq = GenerateRequest.url(url);
        } catch (MalformedURLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Invalid URL: " + url);
            return null;
        }
        genReq.variant(Variant.valueOf(variant.name()));
        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(url, genReq));
        return null;
    }

    @Override
    public SkinData getByFile(String filePath, SkinData.SkinVariant variant) {
        SkinData cached = tryToGetFromCache(filePath, variant);
        if (cached != null) {
            return cached;
        }

        File file = new File(SKINS_DIRECTORY + filePath);
        if (!file.exists()) {
            FancyNpcs.getInstance().getFancyLogger().error("File does not exist: " + filePath);
            return null;
        }

        GenerateRequest genReq = GenerateRequest.upload(file);
        genReq.variant(Variant.valueOf(variant.name()));
        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(filePath, genReq));
        return null;
    }

    @EventHandler
    public void onSkinGenerated(SkinGeneratedEvent event) {
        if (event.getSkin() == null) {
            return;
        }

        SkinData skinData = new SkinData(
                event.getId(),
                event.getSkin().variant() == Variant.SLIM ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO,
                event.getSkin().texture().data().value(),
                event.getSkin().texture().data().signature()
        );

        cacheSkin(skinData);

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            SkinData skin = npc.getData().getSkin();

            if (skin == null)
                continue;

            if (skin.getIdentifier().equals(event.getId())) {
                npc.getData().setSkin(skinData);
                npc.removeForAll();
                npc.spawnForAll();
                FancyNpcs.getInstance().getFancyLogger().info("Updated skin for NPC: " + npc.getData().getName());
            }
        }
    }

    private SkinData tryToGetFromCache(String identifier, SkinData.SkinVariant variant) {
//        FancyNpcs.getInstance().getFancyLogger().debug("Trying to get skin from mem cache: " + identifier);

        SkinCacheData data = memCache.getSkin(identifier);
        if (data != null) {
            if (data.skinData().getVariant() != variant) {
                return null;
            }

            return data.skinData();
        }

//        FancyNpcs.getInstance().getFancyLogger().debug("Trying to get skin from file cache: " + identifier);

        data = fileCache.getSkin(identifier);
        if (data != null) {
            if (data.skinData().getVariant() != variant) {
                return null;
            }

            memCache.addSkin(data.skinData());
            return data.skinData();
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Skin not found in cache: " + identifier);

        return null;
    }

    private void cacheSkin(SkinData skinData) {
        memCache.addSkin(skinData);
        fileCache.addSkin(skinData);
    }

    public SkinCache getFileCache() {
        return fileCache;
    }

    public SkinCache getMemCache() {
        return memCache;
    }
}
