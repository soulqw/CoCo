package com.qw.photo.work

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.dispose.WorkThread
import com.qw.photo.exception.ActivityStatusException
import com.qw.photo.exception.BaseException
import com.qw.photo.pojo.PickParams
import com.qw.photo.pojo.PickResult
import java.io.File

/**
 * Created by rocket on 2019/6/18.
 */
class PickPhotoWorker(handler: IAcceptActivityResultHandler) : BaseWorker<PickParams, PickResult>(handler) {
    override fun start(callBack: GetImageCallBack<PickResult>) {
        val activity = mHandler.provideActivity()
        activity ?: return
        if (!checkStatus(activity, callBack)) return
        pickPhoto(activity, callBack)
    }

    private fun checkStatus(activity: Activity, callBack: GetImageCallBack<PickResult>): Boolean {
        //check permission first
        Utils.checkNecessaryPermissions(activity)
        //check activity status
        if (!Utils.isActivityAvailable(activity)) {
            callBack.onFailed(ActivityStatusException())
            return false
        }
        return true
    }

    private fun pickPhoto(activity: Activity, callBack: GetImageCallBack<PickResult>) {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (null === pickIntent.resolveActivity(activity.packageManager)) {
            callBack.onFailed(BaseException("activity status error"))
            return
        }
        try {
            mHandler.startActivityResult(
                pickIntent,
                Constant.REQUEST_CODE_IMAGE_PICK
            ) { requestCode: Int, resultCode: Int, data: Intent? ->
                handleResult(requestCode, resultCode, data, callBack, activity)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun handleResult(
        requestCode: Int,
        resultCode: Int,
        intentData: Intent?,
        callBack: GetImageCallBack<PickResult>,
        activity: Activity
    ) {
        if (!Utils.isDefinedRequestCode(requestCode)) {
            return
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            callBack.onCancel()
            return
        }
        if (null != intentData && null != intentData.data) {
            val result = PickResult()
            result.originUri = intentData.data!!
            //判断当前状态是否需要处理
            if (null != intentData.data && Utils.isActivityAvailable(activity)) {
                val localPath = Utils.uriToImagePath(activity, intentData.data!!)
                if (!TextUtils.isEmpty(localPath) && null != mParams.disposer) {
                    disposeImage(localPath!!, mParams.file, mParams.disposer!!, result, callBack)
                    return
                }
            }
            callBack.onSuccess(result)
        } else {
            callBack.onFailed(NullPointerException("null result intentData"))
        }
    }

    /**
     * 处理图片
     * @param originPath 图片原始路径
     * @param targetFile 图片处理之后的保存文件 可为空
     * @param disposer 图片处理器
     * @param result 结果
     * @param callBack 回调
     */
    private fun disposeImage(
        originPath: String,
        targetFile: File?,
        disposer: ImageDisposer,
        result: PickResult,
        callBack: GetImageCallBack<PickResult>
    ) {
        disposer.dispose(originPath, targetFile, object : CompressListener {
            override fun onStart(path: String) {
                callBack.onDisposeStart()
            }

            override fun onFinish(compressed: Bitmap, savedFile: File?) {
                result.compressBitmap = compressed
                result.targetFile = savedFile
                DevUtil.d(Constant.TAG, "onSuccess $result")
                callBack.onSuccess(result)
                WorkThread.release()
            }

            override fun onError(e: Exception) {
                callBack.onFailed(e)
            }
        })
    }

}