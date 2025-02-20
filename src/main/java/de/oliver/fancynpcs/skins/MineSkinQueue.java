package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.skins.api.MineSkinAPI;
import de.oliver.fancynpcs.skins.api.RatelimitException;
import org.mineskin.data.SkinInfo;
import org.mineskin.request.GenerateRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MineSkinQueue {

    private final static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(10, new ThreadFactoryBuilder()
            .setNameFormat("MineSkinQueue")
            .build());
    private static MineSkinQueue INSTANCE;

    private final MineSkinAPI api;
    private final Queue<SkinRequest> queue;

    private long nextRequestTime = System.currentTimeMillis();

    private MineSkinQueue() {
        this.queue = new LinkedList<>();
        this.api = new MineSkinAPI(EXECUTOR);

        run();
    }

    public static MineSkinQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MineSkinQueue();
        }

        return INSTANCE;
    }

    private void run() {
        EXECUTOR.scheduleAtFixedRate(this::poll, 5, 1, TimeUnit.SECONDS);
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            return;
        }

        SkinRequest req = this.queue.poll();
        if (req == null) {
            return;
        }

        try {
            SkinInfo skin = this.api.generateSkin(req.request());
            new SkinGeneratedEvent(req.id(), skin).callEvent();
        } catch (RatelimitException e) {
            nextRequestTime = e.getNextRequestTime() + 100;
            this.queue.add(req);
            FancyNpcs.getInstance().getFancyLogger().debug("Ratelimited by MineSkin, retrying in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
        } finally {
            this.nextRequestTime = System.currentTimeMillis();
        }
    }

    public void add(SkinRequest req) {
        this.queue.add(req);
    }


    public record SkinRequest(String id, GenerateRequest request) {
    }
}
