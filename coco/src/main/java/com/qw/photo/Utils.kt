package com.qw.photo

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.dispose.WorkThread
import com.qw.photo.exception.MissPermissionException
import com.qw.photo.pojo.BaseResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
            FileProvider.getUriForFile(context, "${context.applicationInfo.packageName}.coco.provider", file)
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

    @TargetApi(M)
    internal fun checkNecessaryPermissions(activity: Activity?) {
        if (!isActivityAvailable(activity)) {
            return
        }
        val readStorageResult =
                ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStorageResult =
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (readStorageResult != PackageManager.PERMISSION_GRANTED
                || writeStorageResult != PackageManager.PERMISSION_GRANTED
        ) {
            throw MissPermissionException()
        }
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
            e.printStackTrace()
            return false
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
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
    fun <Result : BaseResult> disposeImage(activity: Activity, originPath: String, targetFile: File?,
                                           disposer: ImageDisposer,
                                           result: Result,
                                           callBack: GetImageCallBack<Result>
    ) {
        disposer.dispose(activity, originPath, targetFile, object : CompressListener {
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

    private fun bitmapToBytes(bm: Bitmap): ByteArray {
        var baos: ByteArrayOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            return baos.toByteArray()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}