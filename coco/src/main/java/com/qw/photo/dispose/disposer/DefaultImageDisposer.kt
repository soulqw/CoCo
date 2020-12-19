package com.qw.photo.dispose.disposer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.annotation.IntRange
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.CompressFactory
import com.qw.photo.exception.CompressFailedException
import com.qw.photo.exception.MissCompressStrategyException
import com.qw.photo.exception.NoFileProvidedException
import com.qw.photo.pojo.DisposeResult
import java.io.File


/**
 * the default implement to provided for dispose image for pick or take form system
 * @author cd5160866
 */
open class DefaultImageDisposer : Disposer {

    companion object {

        fun get(): DefaultImageDisposer {
            return DefaultImageDisposer()
                .strategy(CompressStrategy.MATRIX)
                .degree(50)
        }

    }

    private var bitmap: Bitmap? = null

    private var degree: Int = 100

    private var strategy: CompressStrategy? = null

    fun degree(@IntRange(from = 1, to = 100) degree: Int): DefaultImageDisposer {
        this.degree = degree
        return this
    }

    fun strategy(strategy: CompressStrategy): DefaultImageDisposer {
        this.strategy = strategy
        return this
    }

    override fun disposeFile(originPath: String, targetToSaveResult: File?): DisposeResult {
        if (null == strategy) {
            throw MissCompressStrategyException()
        }
        DevUtil.d(Constant.TAG, "start dispose")
        val result = DisposeResult()
        try {
            DevUtil.d(Constant.TAG, "start compress")
            bitmap = CompressFactory.create(strategy!!)
                .compress(originPath, degree)
            //rotate if needed
            DevUtil.d(Constant.TAG, "start Rotate")
            bitmap = correctRotate(originPath, bitmap!!)
        } catch (e: Exception) {
            throw CompressFailedException("error on compress or Rotate $e")
        }
        //check result
        if (null == bitmap) {
            throw CompressFailedException("try to dispose bitmap get a null result")
        }
        result.originPath = originPath
        result.compressBitmap = bitmap!!
        //save file as bitmap if needed
        if (null == targetToSaveResult) {
            throw NoFileProvidedException("DisposeBuilder.fileToSaveResult")
        }
        DevUtil.d(Constant.TAG, "start save bitmap to file")
        val saveResult = Utils.bitmapToFile(targetToSaveResult, bitmap!!)
        if (!saveResult) {
            throw CompressFailedException("save bitmap as file failed")
        }
        result.savedFile = targetToSaveResult
        return result
    }

    @Throws(Exception::class)
    private fun correctRotate(imagePath: String, bitmapOrigin: Bitmap): Bitmap? {
        var bitmap = bitmapOrigin
        val exif = ExifInterface(imagePath)
        val degree = when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
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
