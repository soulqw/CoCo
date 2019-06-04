package com.qw.photo.dispose

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.qw.photo.Utils
import com.qw.photo.callback.CompressListener
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.exception.CompressFailedException
import com.qw.photo.exception.MissCompressStrategyException

import java.io.File


/**
 * 图片处理者，主要是压缩处理
 *
 * @author cd5160866
 */
class ImageDisposer {

    companion object {

        fun getDefault(): ImageDisposer {
            return ImageDisposer()
                .strategy(CompressStrategy.MATRIX)
                .degree(50)
        }
    }

    private var bitmap: Bitmap? = null

    private var degree: Int = 100

    private var strategy: CompressStrategy? = null

    fun degree(degree: Int): ImageDisposer {
        this.degree = degree
        return this
    }

    fun strategy(strategy: CompressStrategy): ImageDisposer {
        this.strategy = strategy
        return this
    }

    fun dispose(originPath: String, targetSaveFile: File?, listener: CompressListener) {
        if (null == strategy) {
            listener.onError(MissCompressStrategyException())
            return
        }
        Log.d("qw", "start dispose")
        listener.onStart(originPath)
        WorkThread.addWork(Runnable {
            try {
                Log.d("qw", "start compress")
                bitmap = CompressFactory
                    .create(strategy!!)
                    .compress(originPath, degree)
                //rotate if needed
                Log.d("qw", "start Rotate")
                bitmap = correctRotate(originPath, bitmap!!)
            } catch (e: Exception) {
                Log.d("qw", "error on compress or Rotate $e")
                listener.onError(e)
                return@Runnable
            }

            //check result
            if (null == bitmap) {
                listener.onError(CompressFailedException("try to dispose bitmap get a null result"))
                Log.d("qw", "error on get bitmap")
                return@Runnable
            }
            //save file as bitmap if needed
            if (null != targetSaveFile) {
                Log.d("qw", "start save bitmap to file")
                val saveResult = Utils.bitmapToFile(targetSaveFile, bitmap!!)
                if (!saveResult) {
                    listener.onError(CompressFailedException("save bitmap as file failed"))
                    return@Runnable
                }
            }
            //post result
            Handler(Looper.getMainLooper()).post {
                Log.d("qw", "all dispose ok")
                listener.onFinish(bitmap!!, targetSaveFile)
            }
        })
    }

    @Throws(Exception::class)
    private fun correctRotate(imagePath: String, bitmap: Bitmap): Bitmap? {
        var bitmap = bitmap
        val exif = ExifInterface(imagePath)
        val ori = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val degree = when (ori) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        if (degree != 0) {
            val m = Matrix()
            m.postRotate(degree.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, m, true
            )
        }
        return bitmap
    }
}
