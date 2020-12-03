package com.qw.photo.work

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.constant.Range
import com.qw.photo.constant.Type
import com.qw.photo.exception.BaseException
import com.qw.photo.functions.PickBuilder
import com.qw.photo.pojo.PickResult


/**
 * Created by rocket on 2019/6/18.
 */
class PickPhotoWorker(iContainer: IContainer, builder: PickBuilder) :
    BaseWorker<PickBuilder, PickResult>(iContainer, builder) {

    override fun start(
        formerResult: Any?,
        callBack: CoCoCallBack<PickResult>
    ) {
        val activity = iContainer.provideActivity()
        activity ?: return
        if (null != mParams.pickCallBack) {
            mParams.pickCallBack!!.onStart()
        }
        pickPhoto(activity, callBack)
    }

    private fun pickPhoto(activity: Activity, callBack: CoCoCallBack<PickResult>) {
        val pickIntent = if (mParams.pickRange == Range.PICK_DICM) {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        } else {
            Intent(Intent.ACTION_GET_CONTENT, null).also {
                when (mParams.fileType) {
                    Type.ALL -> {
                        it.type = "image/*"
                    }
                    Type.GIF -> {
                        it.type = "image/gif"
                    }
                    Type.PNG -> {
                        it.type = "image/png"
                    }
                    Type.JPG -> {
                        it.type = "image/jpg"
                    }
                }
            }
        }
        if (null === pickIntent.resolveActivity(activity.packageManager)) {
            callBack.onFailed(BaseException("activity status error"))
            return
        }
        try {
            iContainer.startActivityResult(
                pickIntent, Constant.REQUEST_CODE_IMAGE_PICK
            ) { _: Int, resultCode: Int, data: Intent? ->
                handleResult(resultCode, data, callBack)
            }
        } catch (e: Exception) {
            callBack.onFailed(e)
        }
    }

    private fun handleResult(
        resultCode: Int,
        intentData: Intent?,
        callBack: CoCoCallBack<PickResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (null != mParams.pickCallBack) {
                mParams.pickCallBack!!.onCancel()
            }
            return
        }
        if (null != intentData && null != intentData.data) {
            val result = PickResult()
            result.originUri = intentData.data!!
            var localPath: String? = null
            try {
                localPath = Utils.uriToImagePath(iContainer.provideActivity()!!, intentData.data!!)
            } catch (e: Exception) {
                DevUtil.e(Constant.TAG, e.toString())
            }
            if (!TextUtils.isEmpty(localPath)) {
                result.localPath = localPath!!
            }
            if (null != mParams.pickCallBack) {
                mParams.pickCallBack!!.onFinish(result)
            }
            callBack.onSuccess(result)
        } else {
            callBack.onFailed(BaseException("null result intentData"))
        }
    }


}