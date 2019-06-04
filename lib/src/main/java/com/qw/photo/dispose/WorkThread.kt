package com.qw.photo.dispose

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author cd5160866
 */
object WorkThread {

    private var executor: ExecutorService? = null

    fun addWork(runnable: Runnable) {
        if (executor == null || executor!!.isShutdown) {
            executor = Executors.newSingleThreadExecutor()
        }
        executor!!.submit(runnable)
    }

    fun release() {
        if (executor != null) {
            executor!!.shutdownNow()
            executor = null
        }
    }
}
