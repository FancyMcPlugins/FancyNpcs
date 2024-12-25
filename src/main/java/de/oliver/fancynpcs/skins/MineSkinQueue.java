package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MineSkinQueue {

    private final static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder()
            .setNameFormat("MineSkinQueue")
            .build());
    private static MineSkinQueue INSTANCE;
    private final Queue<Runnable> queue;

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

    public void add(Runnable runnable) {
        this.queue.add(runnable);
    }

    private void run() {
        EXECUTOR.scheduleAtFixedRate(this::poll, 5, 60, TimeUnit.SECONDS);
    }

    private void poll() {
        System.out.println("Polling queue");
        if (this.queue.isEmpty()) {
            return;
        }

        System.out.println("Running runnables");
        int counter = 0;
        while (!this.queue.isEmpty() && counter < 9) {
            Runnable runnable = this.queue.poll();
            if (runnable != null) {
                runnable.run();
            }
            counter++;
        }
    }
}
