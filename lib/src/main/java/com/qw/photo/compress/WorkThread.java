package com.qw.photo.compress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author cd5160866
 */
public class WorkThread {

    private static ExecutorService executor;

    public static void addWork(Runnable runnable) {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }
        executor.submit(runnable);
    }

    public static void release() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
