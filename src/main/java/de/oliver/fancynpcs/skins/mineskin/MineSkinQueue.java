package de.oliver.fancynpcs.skins.mineskin;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import org.mineskin.data.SkinInfo;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MineSkinQueue {
    private static MineSkinQueue INSTANCE;

    private final MineSkinAPI api;
    private final Queue<SkinRequest> queue;
    private ScheduledFuture<?> scheduler;

    private long nextRequestTime = System.currentTimeMillis();

    private MineSkinQueue() {
        this.queue = new LinkedList<>();
        this.api = new MineSkinAPI(SkinManagerImpl.EXECUTOR);

        run();
    }

    public static MineSkinQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MineSkinQueue();
        }

        return INSTANCE;
    }

    public void run() {
        scheduler = SkinManagerImpl.EXECUTOR.scheduleWithFixedDelay(this::poll, 5, 1, TimeUnit.SECONDS);
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            FancyNpcs.getInstance().getFancyLogger().debug("Retrying to generate skin in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        SkinRequest req = this.queue.poll();
        if (req == null) {
            return;
        }

        try {
            FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from MineSkin: " + req.id());
            SkinInfo skin = this.api.generateSkin(req.request());
            if (skin == null) {
                this.nextRequestTime = System.currentTimeMillis();
                return;
            }

            SkinData skinData = new SkinData(
                    req.id(),
                    skin.variant() == Variant.SLIM ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO,
                    skin.texture().data().value(),
                    skin.texture().data().signature()
            );
            new SkinGeneratedEvent(req.id(), skinData).callEvent();
        } catch (RatelimitException e) {
            this.nextRequestTime = e.getNextRequestTime();
            this.queue.add(req);
            FancyNpcs.getInstance().getFancyLogger().debug("Failed to generate skin: ratelimited by MineSkin, retrying in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        this.nextRequestTime = System.currentTimeMillis();
    }

    public void add(SkinRequest req) {
        // check if request is already in queue
        for (SkinRequest r : this.queue) {
            if (r.id().equals(req.id())) {
                return;
            }
        }

        this.queue.add(req);
    }

    public void clear() {
        this.queue.clear();
    }

    public ScheduledFuture<?> getScheduler() {
        return scheduler;
    }

    public record SkinRequest(String id, GenerateRequest request) {
    }
}
