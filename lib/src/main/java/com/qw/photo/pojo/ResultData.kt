package com.qw.photo.pojo

import android.graphics.Bitmap
import android.net.Uri
import java.io.File


/**
 *
 * @author cd5160866
 */
class ResultData {
    /**
     * 拍照不指定本地路径有此参数
     */
    var thumbnailData: Bitmap? = null
    /**
     * 拍照完成后写成的文件
     */
    var file: File? = null
    /**
     * 选择图片生成的url
     */
    var uri: Uri? = null
    /**
     * 如果制定了压缩的话
     */
    var compressBitmap: Bitmap? = null
}
