package com.qw.photo.work

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.CompressListener
import com.qw.photo.constant.Constant
import com.qw.photo.constant.Host
import com.qw.photo.dispose.WorkThread
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.exception.BadConvertException
import com.qw.photo.exception.PickNoResultException
import com.qw.photo.functions.DisposeBuilder
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.NullPointerException

/**
 *Author: 思忆
 *Date: Created in 2020/9/8 3:29 PM
 */
class DisposeWorker(handler: IContainer, builder: DisposeBuilder) :
    BaseWorker<DisposeBuilder, DisposeResult>(handler, builder) {

    override fun start(
        formerResult: Any?,
        callBack: CoCoCallBack<DisposeResult>
    ) {
        try {
            //if former has result  convert to current params
            convertFormerResultToCurrent(formerResult)
            dispose(
                iContainer.getLifecycleHost(),
                mParams.originPath,
                mParams.targetFile,
                mParams.disposer,
                runCompressListener(callBack)
            )
        } catch (e: Exception) {
            if (e is BadConvertException) {
                generateLocalPathAndHandResultWhenConvertUriFailed(
                    iContainer.provideActivity()!!,
                    iContainer.getLifecycleHost(),
                    e.result,
                    callBack
                )
            } else {
                callBack.onFailed(e)
            }
        }
    }

    private fun convertFormerResultToCurrent(
        formerResult: Any?
    ) {
        if (null == formerResult) {
            return
        }

        if (formerResult is TakeResult) {
            mParams.originPath = formerResult.savedFile!!.absolutePath
            if (null == mParams.targetFile) {
                mParams.targetFile = formerResult.savedFile!!
            }
        }
        if (formerResult is PickResult) {
            var localPath: String? = null
            try {
                localPath =
                    Utils.uriToImagePath(iContainer.provideActivity()!!, formerResult.originUri)
            } catch (e: Exception) {
                DevUtil.e(Constant.TAG, e.toString())
            }
            if (!TextUtils.isEmpty(localPath)) {
                mParams.originPath = localPath
                if (null == mParams.targetFile) {
                    mParams.targetFile = File(mParams.originPath)
                }
            } else {
                throw BadConvertException(formerResult)
            }
        }
    }

    private fun dispose(
        lifecycleHost: Host,
        originPath: String?,
        targetSaveFile: File?,
        disposer: ImageDisposer,
        listener: CompressListener
    ) {
        if (TextUtils.isEmpty(originPath)) {
            listener.onError(NullPointerException("try to dispose image with an null path"))
            return
        }
        listener.onStart(originPath!!)
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
    private fun generateLocalPathAndHandResultWhenConvertUriFailed(
        activity: Activity,
        host: Host,
        result: PickResult,
        callBack: CoCoCallBack<DisposeResult>

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
                runOnUIThread(Runnable {
                    dispose(
                        host,
                        result.localPath,
                        mParams.targetFile,
                        mParams.disposer,
                        runCompressListener(callBack)
                    )
                })
            } catch (e: Exception) {
                runOnUIThread(Runnable { callBack.onFailed(e) })
            }
        })
    }

    private fun runCompressListener(
        callBack: CoCoCallBack<DisposeResult>
    ): CompressListener {
        return object : CompressListener {
            override fun onStart(path: String) {
                if (null != mParams.disposeCallBack) {
                    mParams.disposeCallBack!!.onStart()
                }
            }

            override fun onFinish(disposeResult: DisposeResult) {
                DevUtil.d(Constant.TAG, "onSuccess $disposeResult")
                if (null != mParams.disposeCallBack) {
                    mParams.disposeCallBack!!.onFinish(disposeResult)
                }
                callBack.onSuccess(disposeResult)
                WorkThread.release()
            }

            override fun onError(e: Exception) {
                if (Utils.isOnMainThread()) {
                    callBack.onFailed(e)
                    return
                }
                Handler(Looper.getMainLooper()).post {
                    callBack.onFailed(e)
                }
            }
        }
    }

    private fun checkContainerStatus(host: Host, currentStepDesc: String): Boolean {
        if (!Utils.isHostAvailable(host)) {
            DevUtil.d(Constant.TAG, "host is disabled after $currentStepDesc")
            return false
        }
        return true
    }

    private fun runOnUIThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    private fun bitmap2Bytes(bm: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

}