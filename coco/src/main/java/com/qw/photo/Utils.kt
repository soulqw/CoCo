package com.qw.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Host
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.DisposerManager
import com.qw.photo.dispose.WorkThread
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.BaseResult
import java.io.*


/**
 *
 * @author cd5160866
 */
object Utils {

    internal fun createUriFromFile(context: Context, file: File): Uri? {
        if (!file.exists()) {
            file.mkdir()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.applicationInfo.packageName}.coco.provider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }

    internal fun isActivityAvailable(activity: Activity?): Boolean {
        if (null == activity) {
            return false
        }
        if (activity.isFinishing) {
            DevUtil.d(Constant.TAG, " activity is finishing :" + activity.javaClass.simpleName)
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
            DevUtil.d(Constant.TAG, " activity is destroyed :" + activity.javaClass.simpleName)
            return false
        }
        return true
    }

    internal fun isHostAvailable(host: Host): Boolean {
        return host.getStatus() == Host.Status.LIVE
    }

    @SuppressLint("Recycle")
    fun uriToImagePath(context: Context, uri: Uri): String? {
        val resolver: ContentResolver? = context.contentResolver ?: return null
        var cursor = resolver!!.query(uri, null, null, null, null)
        if (cursor == null || cursor.count <= 0) {
            return null
        }
        cursor.moveToFirst()
        var column = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        if (column < 0) {
            val columnTemp = cursor.getColumnIndex("document_id")
            var imageId: String? = "" + cursor.getString(columnTemp)
            if (imageId == null || !imageId.contains("image:")) {
                return null
            }
            imageId = "content://media/external/images/media/" + imageId.substring(6)
            cursor = resolver.query(Uri.parse(imageId), null, null, null, null)
            if (cursor == null || cursor.count <= 0) {
                return null
            }
            cursor.moveToFirst()
            column = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            if (column < 0) {
                cursor.close()
                return null
            }
        }
        val imagePath = cursor.getString(column)
        cursor.close()
        return imagePath
    }

    fun bitmapToFile(file: File, bm: Bitmap): Boolean {
        var fos: FileOutputStream? = null
        try {
            if (!file.exists())
                file.createNewFile()
            fos = FileOutputStream(file)
            fos.write(bitmapToBytes(bm))
        } catch (e: IOException) {
            DevUtil.e(Constant.TAG, e.toString())
            return false
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                DevUtil.e(Constant.TAG, e.toString())
            }
        }
        return true
    }

    /**
     * 处理图片
     * @param originPath 图片原始路径
     * @param targetFile 图片处理之后的保存文件 可为空
     * @param disposer 图片处理器
     * @param result 结果
     * @param callBack 回调
     */
//    fun <Result : BaseResult> disposeImage(
//        host: Host, originPath: String, targetFile: File?,
//        disposer: ImageDisposer,
//        result: Result,
//        callBack: GetImageCallBack<Result>
//    ) {
//        DisposerManager.dispose(host, originPath, targetFile, disposer, object : CompressListener {
//            override fun onStart(path: String) {
//                callBack.onDisposeStart()
//            }
//
//            override fun onFinish(disposeResult: BaseResult) {
//                result.compressBitmap = disposeResult.compressBitmap
//                result.targetFile = disposeResult.targetFile
//                result.extra = disposeResult.extra
//                DevUtil.d(Constant.TAG, "onSuccess $result")
//                callBack.onSuccess(result)
//                WorkThread.release()
//            }
//
//            override fun onError(e: Exception) {
//                if (isOnMainThread()) {
//                    callBack.onFailed(e)
//                    return
//                }
//                Handler(Looper.getMainLooper()).post {
//                    callBack.onFailed(e)
//                }
//            }
//        })
//    }

    internal fun getBitmapFromFile(filePath: String): Bitmap? {
        var bis: BufferedInputStream? = null
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(filePath)
            bis = BufferedInputStream(fis)
            BitmapFactory.decodeStream(bis, null, null)
            fis.close()
            bis.close()
            fis = FileInputStream(filePath)
            bis = BufferedInputStream(fis)
            return BitmapFactory.decodeStream(bis, null, null)
        } finally {
            try {
                bis!!.close()
                fis!!.close()
            } catch (e: IOException) {
                DevUtil.e(Constant.TAG, e.toString())
                return null
            }
        }
    }

    private fun isOnMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    private fun bitmapToBytes(bm: Bitmap): ByteArray {
        var baos: ByteArrayOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            return baos.toByteArray()
        } finally {
            try {
                baos!!.close()
            } catch (e: IOException) {
                DevUtil.e(Constant.TAG, e.toString())
            }
        }
    }
}