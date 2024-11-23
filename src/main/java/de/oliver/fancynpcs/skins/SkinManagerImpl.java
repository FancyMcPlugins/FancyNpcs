package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinManager;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.CodeAndMessage;
import org.mineskin.data.SkinInfo;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.JobResponse;
import org.mineskin.response.MineSkinResponse;
import org.mineskin.response.QueueResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SkinManagerImpl implements SkinManager {

    private final ScheduledExecutorService executor;
    private final MineSkinClient client;
    private final SkinCache cache;

    public SkinManagerImpl(SkinCache cache) {
        this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("FancyNpcs-Skins")
                .build());

        this.client = MineSkinClient.builder()
                .requestHandler(JsoupRequestHandler::new)
                .userAgent("FancyNpcs")
                .getExecutor(executor)
                .generateExecutor(executor)
                .generateRequestScheduler(executor)
                .generateRequestScheduler(executor)
                .jobCheckScheduler(executor)
                .build();

        this.cache = cache;
    }

    @Override
    public SkinData getByUUID(UUID uuid) {
        GenerateRequest genReq = GenerateRequest.user(uuid);
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        return new SkinData(
                uuid.toString(),
                SkinData.SkinType.UUID,
                SkinData.SkinVariant.DEFAULT,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );
    }

    @Override
    public SkinData getByUsername(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username); // TODO: implement a better way to get the UUID

        GenerateRequest genReq = GenerateRequest.user(offlinePlayer.getUniqueId());
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        return new SkinData(
                username,
                SkinData.SkinType.USERNAME,
                SkinData.SkinVariant.DEFAULT,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );
    }

    @Override
    public SkinData getByURL(String url) {
        GenerateRequest genReq;
        try {
            genReq = GenerateRequest.url(url);
        } catch (MalformedURLException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Invalid URL: " + url);
            return null;
        }

        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        return new SkinData(
                url,
                SkinData.SkinType.URL,
                SkinData.SkinVariant.DEFAULT,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );
    }

    @Override
    public SkinData getByFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            FancyNpcs.getInstance().getFancyLogger().error("File does not exist: " + filePath);
            return null;
        }

        GenerateRequest genReq = GenerateRequest.upload(file);
        SkinInfo skinInfo = executeRequest(genReq);

        if (skinInfo == null) {
            return null;
        }

        return new SkinData(
                filePath,
                SkinData.SkinType.FILE,
                SkinData.SkinVariant.DEFAULT,
                skinInfo.texture().data().value(),
                skinInfo.texture().data().signature()
        );
    }

    @Override
    public SkinData get(String name, String value, String signature) {
        return new SkinData(
                name,
                SkinData.SkinType.VALUE_SIGNATURE,
                SkinData.SkinVariant.DEFAULT,
                value,
                signature
        );
    }

    private SkinInfo executeRequest(GenerateRequest req) {
        // submit job to the queue
        CompletableFuture<QueueResponse> queueResp = client.queue().submit(req);

        // wait for job completion
        CompletableFuture<JobResponse> jobResp = queueResp.thenCompose(
                queueResponse -> queueResponse.getJob().waitForCompletion(client)
        );

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
                Optional<CodeAndMessage> detailsOptional = response.getErrorOrMessage();
                detailsOptional.ifPresent(details -> {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not fetch skin: " + details.code() + ": " + details.message());
                });
            }
            return null;
        });

        return skinResp.join();
    }
}
