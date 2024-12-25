package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinManager;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import de.oliver.fancynpcs.skins.cache.SkinCacheData;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.CodeAndMessage;
import org.mineskin.data.JobReference;
import org.mineskin.data.SkinInfo;
import org.mineskin.data.Variant;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.MineSkinResponse;
import org.mineskin.response.QueueResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SkinManagerImpl implements SkinManager {

    private final String SKINS_DIRECTORY = "plugins/FancyNpcs/skins/";

    private final ScheduledExecutorService executor;
    private final MineSkinClient client;

    private final SkinCache fileCache;
    private final SkinCache memCache;

    public SkinManagerImpl(SkinCache fileCache, SkinCache memCache) {
        this.executor = Executors.newScheduledThreadPool(5, new ThreadFactoryBuilder()
                .setNameFormat("FancyNpcs-Skins")
                .build());

        this.client = MineSkinClient.builder()
                .requestHandler(JsoupRequestHandler::new)
                .apiKey(FancyNpcs.getInstance().getFancyNpcConfig().getMineSkinApiKey())
                .userAgent("FancyNpcs")
                .getExecutor(executor)
                .generateExecutor(executor)
                .generateRequestScheduler(executor)
                .generateRequestScheduler(executor)
                .jobCheckScheduler(executor)
                .build();

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
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        SkinData skinData = new SkinData(
                uuid.toString(),
                variant,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );

        cacheSkin(skinData);
        return skinData;
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
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        SkinData skinData = new SkinData(
                uuid.toString(),
                variant,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );

        cacheSkin(skinData);
        return skinData;
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

        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        SkinData skinData = new SkinData(
                url,
                variant,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );

        cacheSkin(skinData);
        return skinData;
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
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        SkinData skinData = new SkinData(
                filePath,
                variant,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );

        cacheSkin(skinData);
        return skinData;
    }

    private SkinInfo executeRequest(GenerateRequest req) {
        FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from MineSkin: " + req.getClass().getSimpleName());

        // submit job to the queue
        CompletableFuture<QueueResponse> queueResp = client.queue().submit(req);

        queueResp.exceptionally(throwable -> {
            FancyNpcs.getInstance().getFancyLogger().error("Could not submit job to MineSkin queue: " + throwable.getMessage());
            return null;
        });

        // wait for job completion
        CompletableFuture<JobReference> jobResp = queueResp.thenCompose(
                queueResponse -> queueResponse.getJob().waitForCompletion(client)
        );

        jobResp.exceptionally(throwable -> {
            FancyNpcs.getInstance().getFancyLogger().error("Could not wait for job completion: " + throwable.getMessage());
            return null;
        });

        // get skin from job or load it from the API
        CompletableFuture<SkinInfo> skinResp = jobResp.thenCompose(
                jobResponse -> jobResponse.getOrLoadSkin(client)
        );

        // handle exceptions
        skinResp.exceptionally(throwable -> {
            if (throwable instanceof CompletionException completionException) {
                throwable = completionException.getCause();
            }

            if (throwable instanceof MineSkinRequestException requestException) {
                MineSkinResponse<?> response = requestException.getResponse();

                for (CodeAndMessage error : response.getErrors()) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not fetch skin: " + error.code() + ": " + error.message());
                }

//                Optional<CodeAndMessage> detailsOptional = response.getErrorOrMessage();
//                detailsOptional.ifPresent(details -> {
//                    FancyNpcs.getInstance().getFancyLogger().warn("Could not fetch skin: " + details.code() + ": " + details.message());
//                });
            }
            return null;
        });

        try {
            SkinInfo skinInfo = skinResp.join();
            return skinInfo;
        } catch (Exception e) {
            System.out.println("Error in mineskin req: " + e.getMessage());
            return null;
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

    public ScheduledExecutorService getExecutor() {
        return executor;
    }
}
