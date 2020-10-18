package com.qw.photo.work

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.exception.BadConvertException
import com.qw.photo.exception.BaseException
import com.qw.photo.functions.CropBuilder
import com.qw.photo.pojo.CropResult
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import java.io.File

/**
 * The Crop Worker to crop image
 * @author Hebe
 * Date: 2020/10/9
 */

class CropWorker(handler: IContainer, builder: CropBuilder) :
    BaseWorker<CropBuilder, CropResult>(handler, builder) {

    override fun start(formerResult: Any?, callBack: CoCoCallBack<CropResult>) {
        convertFormerResultToCurrent(formerResult)
        if (mParams.cropCallBack != null) {
            mParams.cropCallBack!!.onStart()
        }
        if (mParams.originFile == null) {
            callBack.onFailed(BaseException("crop file is null"))
            return
        }
        val activity = iContainer.provideActivity()
        activity ?: return
        val uri =
            Utils.createUriFromFile(iContainer.provideActivity() as Context, mParams.originFile!!)
        val intent = routeToCrop(uri, mParams.cropWidth, mParams.cropHeight)
        iContainer.startActivityResult(
            intent,
            Constant.REQUEST_CODE_CORP_IMAGE
        ) { _: Int, resultCode: Int, _: Intent? ->
            handleResult(resultCode, callBack)
        }
    }

    private fun handleResult(
        resultCode: Int,
        callBack: CoCoCallBack<CropResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (null != mParams.cropCallBack) {
                mParams.cropCallBack!!.onCancel()
            }
            return
        }
        if (mParams.afterCropFile.exists() && mParams.afterCropFile.length() > 0) {
            val result = CropResult()
            result.originFile = mParams.originFile
            result.savedFile = mParams.afterCropFile
            result.cropBitmap = Utils.getBitmapFromFile(mParams.afterCropFile.absolutePath)!!
            if (null != mParams.cropCallBack) {
                mParams.cropCallBack!!.onFinish(result)
            }
            callBack.onSuccess(result)
        } else {
            callBack.onFailed(BaseException("after crop file is null or size == 0"))
        }
    }

    private fun routeToCrop(uri: Uri?, cropWidth: Int, cropHeight: Int): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", true)
        if (Build.MANUFACTURER == "HUAWEI") {
            if (cropWidth == cropHeight) {
                intent.putExtra("aspectX", 9998)
                intent.putExtra("aspectY", 9999)
            } else {
                intent.putExtra("aspectX", cropWidth)
                intent.putExtra("aspectY", cropHeight)
            }
        } else {
            intent.putExtra("aspectX", cropWidth)
            intent.putExtra("aspectY", cropHeight)
        }
        intent.putExtra("outputX", cropWidth)
        intent.putExtra("outputY", cropHeight)
        intent.putExtra("return-data", false)
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Utils.createUriFromFile(iContainer.provideActivity()!!, mParams.afterCropFile)
        )
        DevUtil.d("EXTRA_OUTPUT", mParams.afterCropFile.absolutePath)
        return intent
    }

    private fun convertFormerResultToCurrent(
        formerResult: Any?
    ) {
        if (null == formerResult) {
            return
        }

        if (formerResult is TakeResult) {
            mParams.originFile = formerResult.savedFile!!
        }
        if (formerResult is PickResult) {
            var localPath: String? = null
            try {
                localPath =
                    Utils.uriToImagePath(iContainer.provideActivity()!!, formerResult.originUri)
                if (!localPath.isNullOrBlank()) {
                    val f = File(localPath)
                    if (f.exists()) {
                        mParams.originFile = f
                    } else {
                        throw BadConvertException(formerResult)
                    }
                }
            } catch (e: Exception) {
                DevUtil.e(Constant.TAG, e.toString())
            }
            if (localPath.isNullOrBlank()) {
                throw BadConvertException(formerResult)
            }
        }
        if (formerResult is DisposeResult) {
            // TODO: 2020/10/9 这里需要优化
            mParams.originFile = File(formerResult.originPath)
        }
    }
}