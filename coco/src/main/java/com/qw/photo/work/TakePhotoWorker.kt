package com.qw.photo.work

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
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
import com.qw.photo.pojo.TakeParams
import com.qw.photo.pojo.TakeResult
import java.io.File

/**
 * Created by rocket on 2019/6/18.
 */
class TakePhotoWorker(handler: IAcceptActivityResultHandler) : BaseWorker<TakeParams, TakeResult>(handler) {

    override fun start(callBack: GetImageCallBack<TakeResult>) {
        val activity = mHandler.provideActivity()
        activity ?: return
        if (!checkStatus(activity, callBack)) return
        takePhoto(activity, callBack)
    }

    private fun checkStatus(activity: Activity, callBack: GetImageCallBack<TakeResult>): Boolean {
        //check permission first
        Utils.checkNecessaryPermissions(activity)
        //check activity status
        if (!Utils.isActivityAvailable(activity)) {
            callBack.onFailed(ActivityStatusException())
            return false
        }
        return true
    }

    private fun takePhoto(activity: Activity, callBack: GetImageCallBack<TakeResult>) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (null === takePictureIntent.resolveActivity(activity.packageManager)) {
            callBack.onFailed(BaseException("activity status error"))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        //用户指定了目标文件路径
        if (null != mParams.file) {
            try {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Utils.createUriFromFile((activity) as Context, mParams.file!!)
                )
            } catch (e: Exception) {
                DevUtil.e(Constant.TAG, e.toString())
            }
        }
        try {
            mHandler.startActivityResult(
                takePictureIntent,
                Constant.REQUEST_CODE_IMAGE_CAPTURE
            ) { requestCode: Int, resultCode: Int, data: Intent? ->
                handleResult(requestCode, resultCode, data, callBack)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun handleResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callBack: GetImageCallBack<TakeResult>
    ) {
        if (!Utils.isDefinedRequestCode(requestCode)) {
            return
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            callBack.onCancel()
            return
        }
        if (null != mParams.file) {
            val result = TakeResult()
            result.targetFile = mParams.file
            val targetPath = result.targetFile!!.absolutePath
            //判断当前状态是否需要处理
            if (!TextUtils.isEmpty(targetPath) && null != mParams.disposer) {
                disposeImage(targetPath, mParams.file, mParams.disposer!!, result, callBack)
                return
            }
            callBack.onSuccess(result)
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
        result: TakeResult,
        callBack: GetImageCallBack<TakeResult>
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