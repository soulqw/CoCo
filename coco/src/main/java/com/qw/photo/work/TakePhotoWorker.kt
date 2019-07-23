package com.qw.photo.work

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.exception.BaseException
import com.qw.photo.pojo.TakeParams
import com.qw.photo.pojo.TakeResult

/**
 * Created by rocket on 2019/6/18.
 */
class TakePhotoWorker(handler: IAcceptActivityResultHandler) :
    BaseWorker<TakeParams, TakeResult>(handler) {

    override fun start(callBack: GetImageCallBack<TakeResult>) {
        val activity = mHandler.provideActivity()
        activity ?: return
        takePhoto(activity, callBack)
    }

    private fun takePhoto(activity: Activity, callBack: GetImageCallBack<TakeResult>) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        setCameraFace(takePictureIntent)
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
            ) { _: Int, resultCode: Int, _: Intent? ->
                handleResult(resultCode, callBack)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun setCameraFace(intent: Intent) {
        var face = Camera.CameraInfo.CAMERA_FACING_BACK
        when (mParams.cameraFace) {
            TakeParams.FRONT -> face =Camera.CameraInfo.CAMERA_FACING_FRONT
        }
        intent.putExtra("android.intent.extras.CAMERA_FACING", face)
    }

    private fun handleResult(
        resultCode: Int,
        callBack: GetImageCallBack<TakeResult>
    ) {
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
                Utils.disposeImage(targetPath, mParams.file, mParams.disposer!!, result, callBack)
                return
            }
            callBack.onSuccess(result)
        }
    }
}