package com.qw.photo.compress

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix


/**
 * @author cd5160866
 */
class MatrixCompressor : ICompress {

    @Throws(Exception::class)
    override fun compress(path: String, degree: Int): Bitmap? {
        val degreeF = degree.toFloat() / 100f
        var bitmap = BitmapFactory.decodeFile(path)
        val height = bitmap.height
        val width = bitmap.width
        val matrix = Matrix()
        matrix.postScale(degreeF, degreeF)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return bitmap
    }

}