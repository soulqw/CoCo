package com.qw.photo.dispose

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.callback.Host
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.exception.PickNoResultException
import com.qw.photo.pojo.PickParams
import com.qw.photo.pojo.PickResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

    /**
     * 当将uri 转化为 path失败时 将原图在应用路径内生成并处理
     */
    fun generateLocalPathAndHandResultWhenConvertUriFailed(
        activity: Activity,
        host: Host,
        params: PickParams,
        result: PickResult,
        callBack: GetImageCallBack<PickResult>

    ) {
        WorkThread.addWork(Runnable {
            try {
                //generate local path
                val bitmap: Bitmap? =
                    MediaStore.Images.Media.getBitmap(activity.contentResolver, result.originUri)
                if (null == bitmap) {
                    runOnUIThread(Runnable { callBack.onFailed(PickNoResultException()) })
                    return@Runnable
                }
                val file =
                    File(activity.externalCacheDir!!.path + "/" + System.currentTimeMillis() + ".jpg")
                if (!file.exists()) file.createNewFile()
                result.localPath = file.path
                val fos = FileOutputStream(file)
                fos.write(bitmap2Bytes(bitmap))
                fos.close()
                if (!checkContainerStatus(host, "generate local path")) {
                    return@Runnable
                }
                //dispose
                if (null == params.disposer) {
                    runOnUIThread(Runnable { callBack.onSuccess(result) })
                } else {
                    runOnUIThread(Runnable {
                        Utils.disposeImage(
                            host,
                            result.localPath,
                            params.file,
                            params.disposer!!,
                            result,
                            callBack
                        )
                    })
                }
            } catch (e: Exception) {
                runOnUIThread(Runnable { callBack.onFailed(e) })
            }
        })
    }

    private fun runOnUIThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    private fun bitmap2Bytes(bm: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    private fun checkContainerStatus(host: Host, currentStepDesc: String): Boolean {
        if (!Utils.isHostAvailable(host)) {
            DevUtil.d(Constant.TAG, "host is disabled after $currentStepDesc")
            return false
        }
        return true
    }

}