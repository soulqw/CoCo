package com.qw.photo.dispose

import android.graphics.Bitmap
import androidx.annotation.IntRange


/**
 *
 * @author cd5160866
 */
interface ICompress {

    /**
     * @param path the path of the origin file
     * @param degree the degree of the comprepss 0~100
     */
    @Throws(Exception::class)
    fun compress(path: String, @IntRange(from = 1, to = 100) degree: Int): Bitmap?

}