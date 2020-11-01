package com.qw.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.FileProvider
import com.qw.photo.constant.Constant
import com.qw.photo.constant.Host
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


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

    fun getBitmapFromFile(filePath: String): Bitmap? {
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

    internal fun isOnMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    /**
     * 获取屏幕的高度
     */
    internal fun getScreenHeight(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }

    /**
     * 获取屏幕的宽度
     */
    internal fun getScreenWidth(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    internal fun createSDCardFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(context.externalCacheDir!!.path + "/" + timeStamp)
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
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