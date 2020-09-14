package com.qw.photo.work

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.provider.MediaStore
import com.qw.photo.Utils
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.exception.BaseException
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.TakeResult

/**
 * Created by rocket on 2019/6/18.
 */
class TakePhotoWorker(handler: IContainer, builder: TakeBuilder) :
    BaseWorker<TakeBuilder, TakeResult>(handler, builder) {

    override fun start(
        formerResult: Any?,
        callBack: CoCoCallBack<TakeResult>
    ) {
        val activity = iContainer.provideActivity()
        activity ?: return
        if (null != mParams.takeCallBack) {
            mParams.takeCallBack!!.onStart()
        }
        takePhoto(activity, callBack)
    }

    private fun takePhoto(activity: Activity, callBack: CoCoCallBack<TakeResult>) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        setCameraFace(takePictureIntent)
        if (null === takePictureIntent.resolveActivity(activity.packageManager)) {
            callBack.onFailed(BaseException("activity status error"))
            return
        }
        //用户指定了目标文件路径
        if (null != mParams.fileToSave) {
            val uri = Utils.createUriFromFile((activity) as Context, mParams.fileToSave!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.grantUriPermission(
                    activity.packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                activity.revokeUriPermission(
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        try {
            iContainer.startActivityResult(
                takePictureIntent, Constant.REQUEST_CODE_IMAGE_CAPTURE
            ) { _: Int, resultCode: Int, _: Intent? ->
                handleResult(resultCode, callBack)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun setCameraFace(intent: Intent) {
        when (mParams.cameraFace) {
            TakeBuilder.FRONT -> {
                intent.putExtra(
                    "android.intent.extras.CAMERA_FACING",
                    Camera.CameraInfo.CAMERA_FACING_FRONT
                )
            }
        }
    }

    private fun handleResult(
        resultCode: Int,
        callBack: CoCoCallBack<TakeResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (null != mParams.takeCallBack) {
                mParams.takeCallBack!!.onCancel()
            }
            return
        }
        if (null != mParams.fileToSave) {
            val result = TakeResult()
            result.savedFile = mParams.fileToSave
            if (null != mParams.takeCallBack) {
                mParams.takeCallBack!!.onFinish(result)
            }
            callBack.onSuccess(result)
        }
    }

}