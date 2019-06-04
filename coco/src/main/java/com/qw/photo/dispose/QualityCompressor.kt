package com.qw.photo.dispose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.qw.photo.exception.CompressFailedException
import java.io.ByteArrayOutputStream


/**
 *
 * @author cd5160866
 */
class QualityCompressor : ICompress {

    @Throws(Exception::class)
    override fun compress(path: String, degree: Int): Bitmap? {
        var bitmap = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        val result = bitmap.compress(Bitmap.CompressFormat.JPEG, degree, baos)
        if (!result) {
            throw CompressFailedException("Quality dispose failed")
        }
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size)
        baos.close()
        return bitmap
    }

}