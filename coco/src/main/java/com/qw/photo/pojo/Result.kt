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
class TakeResult : SaveFileResult() {

    override fun toString(): String {
        return "TakeResult(savedFile=${savedFile.toString()}, \nextra=$extra)"
    }
}

/**
 * 处理的结果
 */
class DisposeResult : SaveFileResult() {

    var originPath: String? = null

    var compressBitmap: Bitmap? = null

    override fun toString(): String {
        return "DisposeResult(savedFile=${savedFile.toString()},\n extra=$extra,\noriginPath=$originPath, \ncompressBitmap=$compressBitmap)"
    }
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
     * uri转化的本地path
     */
    lateinit var localPath: String

    override fun toString(): String {
        return "PickResult( extra=$extra,\noriginUri=$originUri,\n localPath='$localPath')"
    }

}

