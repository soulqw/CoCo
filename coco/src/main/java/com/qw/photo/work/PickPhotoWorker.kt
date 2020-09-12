package com.qw.photo.work

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.exception.BaseException
import com.qw.photo.functions.PickBuilder
import com.qw.photo.pojo.PickResult


/**
 * Created by rocket on 2019/6/18.
 */
class PickPhotoWorker(iContainer: IContainer) :
    BaseWorker<PickBuilder, PickResult>(iContainer) {

    lateinit var params: PickBuilder

    override fun start(params: PickBuilder, callBack: CoCoCallBack<PickResult>) {
        this.params = params
        val activity = iContainer.provideActivity()
        activity ?: return
        pickPhoto(activity, callBack)
    }

    private fun pickPhoto(activity: Activity, callBack: CoCoCallBack<PickResult>) {
        val pickIntent = if (params.pickRange == PickBuilder.PICK_DICM) {
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
            if (null != params.pickCallBack) {
                params.pickCallBack!!.onCancel()
            }
            return
        }
        if (null != intentData && null != intentData.data) {
            val result = PickResult()
            result.originUri = intentData.data!!
//            //判断当前状态是否可处理
//            if (null != intentData.data && Utils.isActivityAvailable(activity)) {
//                var localPath: String? = null
//                try {
//                    localPath = Utils.uriToImagePath(activity, intentData.data!!)
//                } catch (e: Exception) {
//                    DevUtil.e(Constant.TAG, e.toString())
//                }
//                //uri convert to local path failed
//                //change another way to generate local path
//                if (TextUtils.isEmpty(localPath)) {
//                    DisposerManager.generateLocalPathAndHandResultWhenConvertUriFailed(
//                        activity,
//                        mHandler.getLifecycleHost(),
//                        mParams,
//                        result,
//                        callBack
//                    )
//                    return
//                }
//                result.localPath = localPath!!
//                if (null != mParams.disposer) {
//                    Utils.disposeImage(
//                        mHandler.getLifecycleHost(),
//                        localPath,
//                        mParams.file,
//                        mParams.disposer!!,
//                        result,
//                        callBack
//                    )
//                    return
//                }
//            }
            callBack.onSuccess(result)
        } else {
            callBack.onFailed(BaseException("null result intentData"))
        }
    }


}