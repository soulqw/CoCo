package com.qw.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.qw.photo.constant.Constant
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 *
 * @author cd5160866
 */
object Utils {

    /**
     * 是否是约定好的requestCode
     */
    internal fun isDefinedRequestCode(requestCode: Int): Boolean {
        val inner = Constant.REQUEST_CODE_IMAGE_CAPTURE or
                Constant.REQUEST_CODE_IMAGE_PICK
        return (inner and requestCode) != 0
    }

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
            Log.d("qw", " activity is finishing :" + activity.javaClass.simpleName)
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
            Log.d("qw", " activity is destroyed :" + activity.javaClass.simpleName)
            return false
        }
        return true
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