package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.api.skins.SkinManager;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import de.oliver.fancynpcs.skins.cache.SkinCacheData;
import de.oliver.fancynpcs.skins.mineskin.MineSkinQueue;
import de.oliver.fancynpcs.skins.mojang.MojangQueue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SkinManagerImpl implements SkinManager, Listener {

    public final static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setNameFormat("FancyNpcs-Skins-%d")
                    .build()
    );

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

    @Override
    public SkinData getByUUID(UUID uuid, SkinData.SkinVariant variant) {
        SkinData cached = tryToGetFromCache(uuid.toString(), variant);
        if (cached != null) {
            return cached;
        }

        MojangQueue.get().add(new MojangQueue.SkinRequest(uuid.toString(), variant));

//        GenerateRequest genReq = GenerateRequest.user(uuid);
//        genReq.variant(Variant.valueOf(variant.name()));
//        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(uuid.toString(), genReq));
        return new SkinData(uuid.toString(), variant);
    }

    @Override
    public SkinData getByUsername(String username, SkinData.SkinVariant variant) {
        UUID uuid = UUIDFetcher.getUUID(username);
        if (uuid == null) {
            return null;
        }

        return getByUUID(uuid, variant);
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
        return new SkinData(url, variant);
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
            return new SkinData(filePath, variant);
        }

        GenerateRequest genReq = GenerateRequest.upload(file);
        genReq.variant(Variant.valueOf(variant.name()));
        MineSkinQueue.get().add(new MineSkinQueue.SkinRequest(filePath, genReq));
        return null;
    }

    @EventHandler
    public void onSkinGenerated(SkinGeneratedEvent event) {
        if (event.getSkin() == null || !event.getSkin().hasTexture()) {
            return;
        }

        cacheSkin(event.getSkin());

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            SkinData skin = npc.getData().getSkinData();
            if (skin == null)
                continue;

            if (skin.getIdentifier().equals(event.getId())) {
                npc.getData().setSkinData(event.getSkin());
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

    public void cacheSkin(SkinData skinData) {
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
