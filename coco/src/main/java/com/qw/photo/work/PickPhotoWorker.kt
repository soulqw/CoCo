package com.qw.photo.work

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.text.TextUtils
import com.qw.photo.Utils
import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.exception.BaseException
import com.qw.photo.pojo.PickParams
import com.qw.photo.pojo.PickResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


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
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT, null)
        pickIntent.type = "image/*"
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
                    e.printStackTrace()
                }
                try {
                    if (TextUtils.isEmpty(localPath)) {
                        val uri = intentData.data!!
                        var bitmap =
                            MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri)
                        if (bitmap==null) return
                        bitmap=getPerfectBitmap(bitmap,800)
                        var file: File =
                            File(activity.externalCacheDir.path + "/" + System.currentTimeMillis() + ".jpg")
                        if (!file.exists()) file.createNewFile()
                        localPath = file.path
                        val fos = FileOutputStream(file)
                        fos.write(Bitmap2Bytes(bitmap))
                        fos.close()
                    }
                }catch (e:Exception){
                    callBack.onFailed(e)
                }
                localPath ?: return
                result.targetFile = File(localPath)
                if (!TextUtils.isEmpty(localPath) && null != mParams.disposer) {
                    Utils.disposeImage(
                        mHandler.getLifecycleHost(),
                        localPath!!,
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
            callBack.onFailed(NullPointerException("null result intentData"))
        }
    }

    fun Bitmap2Bytes(bm: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        return baos.toByteArray()
    }

    fun getPerfectBitmap(sourceBitmap: Bitmap, maxSize: Int): Bitmap? {
        val bitmap: Bitmap?
        if (sourceBitmap.width > maxSize || sourceBitmap.height > maxSize) {
            bitmap = if (sourceBitmap.width >= sourceBitmap.height) {
                val beli = sourceBitmap.width / maxSize.toFloat()
                val heighttemp = (sourceBitmap.height / beli).toInt()
                ThumbnailUtils.extractThumbnail(sourceBitmap, maxSize, heighttemp)
            } else {
                val beli = sourceBitmap.height / maxSize.toFloat()
                val widthtemp = (sourceBitmap.width / beli).toInt()
                ThumbnailUtils.extractThumbnail(sourceBitmap, widthtemp, maxSize)
            }
            /**
             * 因为bitmap = ThumbnailUtils.extractThumbnail(bitmaptemp, 1024,
             * heighttemp);中的bitmaptemp和bitmap已经不是指向 同一个内存区域，所以需要回收bitmaptemp。
             */
            if (sourceBitmap != null && !sourceBitmap.isRecycled) {
                sourceBitmap.recycle()
            }
        } else {
            bitmap = sourceBitmap
        }
        return bitmap
    }

}