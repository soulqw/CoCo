package com.qw.photo

import android.os.Handler
import android.os.Looper
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.Host
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.WorkThread
import com.qw.photo.dispose.disposer.ImageDisposer
import java.io.File

/**
 * disposer 管理器
 * @author: george
 * @date: 2020-01-05
 */
internal object DisposerManager {

    /**
     * @param disposer 处理者
     */
    fun dispose(
        lifecycleHost: Host,
        originPath: String,
        targetSaveFile: File?,
        disposer: ImageDisposer,
        listener: CompressListener
    ) {
        listener.onStart(originPath)
        WorkThread.addWork(Runnable {
            try {
                val result = disposer.disposeImage(originPath, targetSaveFile)
                if (!checkContainerStatus(lifecycleHost, "dispose the Image")) {
                    return@Runnable
                }
                //post result
                Handler(Looper.getMainLooper()).post {
                    DevUtil.d(Constant.TAG, "all dispose ok")
                    listener.onFinish(result)
                }
            } catch (e: Exception) {
                DevUtil.d(Constant.TAG, e.toString())
                if (!checkContainerStatus(lifecycleHost, "dispose Image")) {
                    return@Runnable
                }
                listener.onError(e)
            }
        })
    }

    private fun checkContainerStatus(host: Host, tag: String): Boolean {
        if (!Utils.isHostAvailable(host)) {
            DevUtil.d(Constant.TAG, "host is disabled after $tag")
            return false
        }
        return true
    }

}