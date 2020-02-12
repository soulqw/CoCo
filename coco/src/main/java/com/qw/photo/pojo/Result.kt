package com.qw.photo.pojo

import android.graphics.Bitmap
import android.net.Uri
import java.io.File


/**
 *
 * @author cd5160866
 */
/**
 * 通用的结果
 */
open class BaseResult {
    /**
     * 完成后写成的文件
     */
    var targetFile: File? = null

    /**
     * 如果指定了压缩的话
     */
    var compressBitmap: Bitmap? = null

    /**
     * 如果需要额外的参数的话
     */
    var extra: Any? = null
}

/**
 * 拍照的结果
 */
class TakeResult : BaseResult()

/**
 * 选择的结果
 */
class PickResult : BaseResult() {
    /**
     * 选择图片的uri
     */
    lateinit var originUri: Uri

    /**
     * 本地path
     */
    lateinit var localPath: String

}
