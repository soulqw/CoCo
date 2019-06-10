package com.qw.photo.dispose

import android.graphics.Bitmap
import android.support.annotation.IntRange


/**
 *
 * @author cd5160866
 */
interface ICompress {

    /**
     * @param path 压缩文件路径
     * @param degree 压缩程度 0~100
     */
    @Throws(Exception::class)
    fun compress(path: String, @IntRange(from = 0, to = 100) degree: Int): Bitmap?

}