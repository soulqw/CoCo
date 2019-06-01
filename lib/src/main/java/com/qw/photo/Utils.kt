package com.qw.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File


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

    fun zoomBitmap(bitmap: Bitmap, radios: Float): Bitmap {
        var bitmap = bitmap
        val height = bitmap.height
        val width = bitmap.width
        val matrix = Matrix()
        matrix.postScale(radios, radios)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return bitmap
    }


}