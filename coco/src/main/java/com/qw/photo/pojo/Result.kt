package com.qw.photo.pojo

import android.graphics.Bitmap
import android.net.Uri
import java.io.File


/**
 *
 * @author cd5160866
 */

open class BaseResult {

    /**
     * you can also set the extra data if needed
     */
    var extra: Any? = null
}

open class SaveFileResult : BaseResult() {
    /**
     * the file that write the result
     */
    var savedFile: File? = null

}

/**
 * the result of take
 */
class TakeResult : SaveFileResult() {

    override fun toString(): String {
        return "TakeResult(savedFile=${savedFile}, \nextra=$extra)"
    }
}

/**
 * The result form dispose
 */
class DisposeResult : SaveFileResult() {

    var originPath: String? = null

    lateinit var compressBitmap: Bitmap

    override fun toString(): String {
        return "DisposeResult(savedFile=${savedFile},\n extra=$extra,\noriginPath=$originPath, \ncompressBitmap=$compressBitmap)"
    }
}

/**
 * The result form pick
 */
class PickResult : BaseResult() {
    /**
     * the URI or the picked file
     */
    lateinit var originUri: Uri

    /**
     * the path that convert form URI
     */
    lateinit var localPath: String

    override fun toString(): String {
        return "PickResult( extra=$extra,\noriginUri=$originUri,\n localPath='$localPath')"
    }

}

