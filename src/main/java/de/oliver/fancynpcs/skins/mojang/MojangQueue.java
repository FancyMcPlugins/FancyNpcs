package de.oliver.fancynpcs.skins.mojang;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import de.oliver.fancynpcs.skins.mineskin.RatelimitException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MojangQueue {

    private static MojangQueue INSTANCE;

    private final Queue<SkinRequest> queue;
    private final MojangAPI api;
    private ScheduledFuture<?> scheduler;
    private long nextRequestTime = System.currentTimeMillis();

    private MojangQueue() {
        this.queue = new LinkedList<>();
        this.api = new MojangAPI();

        run();
    }

    public static MojangQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MojangQueue();
        }

        return INSTANCE;
    }

    public void run() {
        scheduler = SkinManagerImpl.EXECUTOR.scheduleWithFixedDelay(this::pollMany, 5, 1, TimeUnit.SECONDS);
    }

    private void pollMany() {
        for (int i = 0; i < 5; i++) {
            poll();
        }
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            FancyNpcs.getInstance().getFancyLogger().debug("Retrying to fetch skin from Mojang in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        SkinRequest req = this.queue.poll();
        if (req == null) {
            return;
        }

        try {
            FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from Mojang: " + req.uuid());
            SkinData skinData = this.api.fetchSkin(req.uuid(), req.variant());
            new SkinGeneratedEvent(req.uuid(), skinData).callEvent();
        } catch (RatelimitException e) {
            this.nextRequestTime = e.getNextRequestTime();
            this.queue.add(req);
            FancyNpcs.getInstance().getFancyLogger().debug("Failed to generate skin: ratelimited by Mojang, retrying in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        this.nextRequestTime = System.currentTimeMillis();
    }

    public void add(SkinRequest req) {
        // check if request is already in queue
        for (SkinRequest r : this.queue) {
            if (r.uuid().equals(req.uuid())) {
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

    public record SkinRequest(String uuid, SkinData.SkinVariant variant) {
    }

}
