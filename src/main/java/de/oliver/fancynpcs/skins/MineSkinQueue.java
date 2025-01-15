package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.mineskin.data.SkinInfo;
import org.mineskin.request.GenerateRequest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MineSkinQueue {

    private final static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder()
            .setNameFormat("MineSkinQueue")
            .build());
    private static MineSkinQueue INSTANCE;
    private final Queue<SkinRequest> queue;

    private long nextRequestTime = System.currentTimeMillis();

    private MineSkinQueue() {
        this.queue = new LinkedList<>();

        run();
    }

    public static MineSkinQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MineSkinQueue();
        }

        return INSTANCE;
    }

    private void run() {
        EXECUTOR.scheduleAtFixedRate(this::poll, 5, 60, TimeUnit.SECONDS);
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            return;
        }

        SkinRequest req = this.queue.poll();


    }

    public void add(SkinRequest req) {
        this.queue.add(req);
    }


    public record SkinRequest(GenerateRequest request, Consumer<SkinInfo> finish) {
    }
}
