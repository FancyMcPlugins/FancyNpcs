package de.oliver.fancynpcs.skins.api;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

public class MineSkinAPI {

    private final MineSkinClient client;

    public MineSkinAPI(ScheduledExecutorService executor) {
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
    }

    public SkinInfo generateSkin(GenerateRequest req) throws RatelimitException {
        FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from MineSkin: " + req.getClass().getSimpleName());

        try {
            QueueResponse queueResp = client.queue().submit(req).get();
            System.out.println("queueResp = " + queueResp);

            JobReference jobResp = queueResp.getJob().waitForCompletion(client).get();
            System.out.println("jobResp = " + jobResp);

            return jobResp.getOrLoadSkin(client).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MineSkinRequestException requestException) {
                MineSkinResponse<?> response = requestException.getResponse();
                for (CodeAndMessage error : response.getErrors()) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not fetch skin: " + error.code() + ": " + error.message());
                }
            } else {
                FancyNpcs.getInstance().getFancyLogger().error("Error in mineskin request: " + cause.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            FancyNpcs.getInstance().getFancyLogger().error("Thread was interrupted while waiting for skin generation.");
        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().error("Unexpected error in skin generation: " + e.getMessage());
        }

        return null;
    }

}
