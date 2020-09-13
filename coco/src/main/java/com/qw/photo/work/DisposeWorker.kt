package com.qw.photo.work

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.CompressListener
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.DisposerManager
import com.qw.photo.dispose.WorkThread
import com.qw.photo.functions.DisposeBuilder
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.TakeResult

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
        handleResult(formerResult)
        DisposerManager.dispose(
            iContainer.getLifecycleHost(),
            getOriginPath(mParams, formerResult),
            mParams.targetFile,
            mParams.disposer,
            object : CompressListener {
                override fun onStart(path: String) {
                    if (null != mParams.disposeCallBack) {
                        mParams.disposeCallBack!!.onStart()
                    }
                }

                override fun onFinish(disposeResult: DisposeResult) {
                    DevUtil.d(Constant.TAG, "onSuccess $disposeResult")
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
            })
    }

    private fun getOriginPath(
        params: DisposeBuilder,
        formerResult: Any?
    ): String? {
        if (!TextUtils.isEmpty(params.originPath)) {
            return params.originPath
        }
        if (null != formerResult) {
            if (formerResult is TakeResult) {
                return formerResult.savedFile!!.absolutePath;
            }
        }
        return null
    }

    private fun handleResult(
        formerResult: Any?
    ) {
        if (null != formerResult) {
            if (formerResult is TakeResult) {
                mParams.targetFile = formerResult.savedFile!!
            }
        }
    }


}