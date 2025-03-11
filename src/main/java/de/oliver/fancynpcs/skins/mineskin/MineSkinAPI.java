package de.oliver.fancynpcs.skins.mineskin;

import de.oliver.fancynpcs.FancyNpcs;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.CodeAndMessage;
import org.mineskin.data.JobReference;
import org.mineskin.data.SkinInfo;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.MineSkinResponse;
import org.mineskin.response.QueueResponse;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

public class MineSkinAPI {

    private final MineSkinClient client;

    public MineSkinAPI(ScheduledExecutorService executor) {
        this.client = MineSkinClient.builder()
                .requestHandler(JsoupRequestHandler::new)
                .apiKey(FancyNpcs.getInstance().getFancyNpcConfig().getMineSkinApiKey())
                .userAgent("FancyNpcs")
                .timeout(1000 * 3)
                .getExecutor(executor)
                .generateExecutor(executor)
                .generateRequestScheduler(executor)
                .generateRequestScheduler(executor)
                .jobCheckScheduler(executor)
                .build();
    }

    public SkinInfo generateSkin(GenerateRequest req) throws RatelimitException {
        FancyNpcs.getInstance().getFancyLogger().debug("Generating a skin with MineSkinAPI...");

        QueueResponse queueResp = null;
        JobReference jobResp = null;

        try {
            queueResp = client.queue().submit(req).get();
            if (queueResp.getRateLimit().limit().remaining() == 0) {
                // TODO use queueResp.getRateLimit().next() instead
                throw new RatelimitException(System.currentTimeMillis() + 1000 * 10); // retry in next run
            }

            jobResp = queueResp.getJob().waitForCompletion(client).get();

            SkinInfo skinInfo = jobResp.getOrLoadSkin(client).get();

            FancyNpcs.getInstance().getFancyLogger().debug("Skin generated with MineSkinApi: " + skinInfo.toString());
            return skinInfo;
        } catch (RatelimitException e) {
            throw e; // rethrow
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MineSkinRequestException requestException) {
                MineSkinResponse<?> response = requestException.getResponse();
                for (CodeAndMessage error : response.getErrors()) {
                    if (error.code().equals("rate_limit")) {
                        // TODO use queueResp.getRateLimit().next() instead
                        throw new RatelimitException(System.currentTimeMillis() + 1000 * 10); // retry in next run
                    }
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not fetch skin: " + error.code() + ": " + error.message());
                    FancyNpcs.getInstance().getFancyLogger().debug("QueueResp: " + queueResp.toString());
                    FancyNpcs.getInstance().getFancyLogger().debug("JobResp: " + jobResp.toString());
                }
            } else if (cause instanceof SocketTimeoutException timeoutException) {
                FancyNpcs.getInstance().getFancyLogger().warn("Timeout while fetching skin: " + timeoutException.getMessage());
                FancyNpcs.getInstance().getFancyLogger().debug("QueueResp: " + queueResp.toString());
                FancyNpcs.getInstance().getFancyLogger().debug("JobResp: " + jobResp.toString());
                throw new RatelimitException(System.currentTimeMillis() + 1000 * 10); // retry in next run
            } else {
                FancyNpcs.getInstance().getFancyLogger().error("Error in mineskin request: " + cause.getMessage());
                FancyNpcs.getInstance().getFancyLogger().debug("QueueResp: " + queueResp.toString());
                FancyNpcs.getInstance().getFancyLogger().debug("JobResp: " + jobResp.toString());
            }
        } catch (InterruptedException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Thread was interrupted while waiting for skin generation.");
            FancyNpcs.getInstance().getFancyLogger().debug("QueueResp: " + queueResp.toString());
            FancyNpcs.getInstance().getFancyLogger().debug("JobResp: " + jobResp.toString());
        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().error("Unexpected error in skin generation: " + e.getMessage());
            FancyNpcs.getInstance().getFancyLogger().debug("QueueResp: " + queueResp.toString());
            FancyNpcs.getInstance().getFancyLogger().debug("JobResp: " + jobResp.toString());
        }

        return null;
    }

}
