package com.qw.photo.callback

import android.graphics.Bitmap


/**
 *
 * @author cd5160866
 */
interface CompressListener {

    fun onStart(path: String)

    fun onFinish(compressed: Bitmap)

    fun onError(e: Exception)

}