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
open class BaseResultData {
    /**
     * 完成后写成的文件
     */
    var targetFile: File? = null

    /**
     * 如果制指定了压缩的话
     */
    var compressBitmap: Bitmap? = null
}

/**
 * 拍照的结果
 */
class CaptureResult : BaseResultData()

/**
 * 选择的结果
 */
class PickResult : BaseResultData() {
    /**
     * 选择图片的ur
     */
    var uri: Uri? = null
}
