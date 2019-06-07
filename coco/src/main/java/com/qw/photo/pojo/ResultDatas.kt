package com.qw.photo.pojo

import android.graphics.Bitmap
import android.net.Uri
import java.io.File


/**
 *
 * @author cd5160866
 */
open class BaseResultData {
    /**
     * 完成后写成的文件
     */
    var targetFile: File? = null

    /**
     * 如果制定了压缩的话
     */
    var compressBitmap: Bitmap? = null
}

class PickResultData : BaseResultData(){
    /**
     * 选择图片生成的url
     */
    var uri: Uri? = null
}
