package com.qw.photo.callback

import android.graphics.Bitmap
import java.io.File


/**
 *
 * @author cd5160866
 */
interface CompressListener {

    /**
     * 开始压缩
     */
    fun onStart(path: String)

    /**
     * 任务结束
     */
    fun onFinish(compressed: Bitmap, savedFile: File?)

    /**
     * 发生错误
     */
    fun onError(e: Exception)

}