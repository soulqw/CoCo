package com.qw.photo.dispose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix


/**
 * @author cd5160866
 */
class MatrixCompressor : ICompress {

    @Throws(Exception::class)
    override fun compress(path: String, degree: Int): Bitmap? {
        val finalDegree: Int = when {
            degree <= 0 -> 1
            degree > 100 -> 100
            else -> degree
        }
        val degreeF = finalDegree.toFloat() / 100f
        var bitmap = BitmapFactory.decodeFile(path)
        val height = bitmap.height
        val width = bitmap.width
        val matrix = Matrix()
        matrix.postScale(degreeF, degreeF)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return bitmap
    }

}