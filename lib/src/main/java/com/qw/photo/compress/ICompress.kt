package com.qw.photo.compress

import android.graphics.Bitmap


/**
 *
 * @author cd5160866
 */
interface ICompress {

    @Throws(Exception::class)
    fun compress(path: String, degree: Int): Bitmap?

}