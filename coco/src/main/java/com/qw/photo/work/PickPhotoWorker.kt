package com.qw.photo.work

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.Utils
import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.DisposerManager
import com.qw.photo.exception.BaseException
import com.qw.photo.exception.PickNoResultException
import com.qw.photo.pojo.PickParams
import com.qw.photo.pojo.PickResult


/**
 * Created by rocket on 2019/6/18.
 */
class PickPhotoWorker(handler: IAcceptActivityResultHandler) :
    BaseWorker<PickParams, PickResult>(handler) {
    override fun start(callBack: GetImageCallBack<PickResult>) {
        val activity = mHandler.provideActivity()
        activity ?: return
        pickPhoto(activity, callBack)
    }

    private fun pickPhoto(activity: Activity, callBack: GetImageCallBack<PickResult>) {
        val pickIntent = if (mParams.pickRange == PickParams.PICK_DICM) {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        } else {
            Intent(Intent.ACTION_GET_CONTENT, null).also {
                it.type = "image/*"
            }
        }
        if (null === pickIntent.resolveActivity(activity.packageManager)) {
            callBack.onFailed(BaseException("activity status error"))
            return
        }
        try {
            mHandler.startActivityResult(
                pickIntent, Constant.REQUEST_CODE_IMAGE_PICK
            ) { _: Int, resultCode: Int, data: Intent? ->
                handleResult(resultCode, data, callBack, activity)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun handleResult(
        resultCode: Int,
        intentData: Intent?,
        callBack: GetImageCallBack<PickResult>,
        activity: Activity
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            callBack.onCancel()
            return
        }
        if (null != intentData && null != intentData.data) {
            val result = PickResult()
            result.originUri = intentData.data!!
            //判断当前状态是否需要处理
            if (null != intentData.data && Utils.isActivityAvailable(activity)) {
                var localPath: String? = null
                try {
                    localPath = Utils.uriToImagePath(activity, intentData.data!!)
                } catch (e: Exception) {
                    if (e is java.lang.IllegalStateException) {
                        //uri convert to local path failed
                        //change another way to generate local path
                        DisposerManager.generateLocalPathAndHandResultWhenConvertUriFailed(
                            activity,
                            mHandler.getLifecycleHost(),
                            mParams,
                            result,
                            callBack
                        )
                    } else {
                        callBack.onFailed(e)
                        return
                    }
                }
                if (TextUtils.isEmpty(localPath)) {
                    callBack.onFailed(PickNoResultException())
                    return
                }
                result.localPath = localPath!!
                if (null != mParams.disposer) {
                    Utils.disposeImage(
                        mHandler.getLifecycleHost(),
                        localPath,
                        mParams.file,
                        mParams.disposer!!,
                        result,
                        callBack
                    )
                    return
                }
            }
            callBack.onSuccess(result)
        } else {
            callBack.onFailed(BaseException("null result intentData"))
        }
    }

}