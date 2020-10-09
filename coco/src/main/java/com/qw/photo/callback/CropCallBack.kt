package com.qw.photo.callback

import com.qw.photo.pojo.CropResult

/**
 * Crop image CallBack
 * @author Hebe
 * Date: 2020/10/9
 */

interface CropCallBack : BaseFunctionCallBack {

    /**
     * do when get the crop result
     */
    fun onFinish(result: CropResult)

    /**
     * do when crop canceled
     */
    fun onCancel()
}