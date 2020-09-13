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
     * 如果需要额外的参数的话
     */
    var extra: Any? = null
}

open class SaveFileResult : BaseResult() {
    /**
     * 完成后写成的文件
     */
    var savedFile: File? = null
}

/**
 * 拍照的结果
 */
class TakeResult : SaveFileResult()

/**
 * 处理的结果
 */
class DisposeResult : SaveFileResult() {

    var originPath: String? = null

    var compressBitmap: Bitmap? = null

}

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

