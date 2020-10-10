package com.qw.photo.functions

import com.qw.photo.callback.CropCallBack
import com.qw.photo.pojo.CropResult
import com.qw.photo.work.CropWorker
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.Worker
import java.io.File

/**
 * Crop Builder
 * @author Hebe
 * Date: 2020/10/9
 */

class CropBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<CropBuilder, CropResult>(fm){

    var originFile : File? = null
    lateinit var afterCropFile : File
    var cropWidth : Int = 500
    var cropHeight : Int = 500

    internal var cropCallBack: CropCallBack? = null


    /**
     * crop image callback
     * @param callBack CropCallBack
     * @return CropBuilder
     */
    fun callBack(callBack: CropCallBack): CropBuilder {
        this.cropCallBack = callBack
        return this
    }

    /**
     * init crop file
     * @param file File? the file you want crop
     * @return CropBuilder
     */
    fun file(originFile : File?):CropBuilder{
        this.originFile = originFile
        return this
    }

    /**
     * init crop size
     * @param cropWidth Int
     * @param cropHeight Int
     * @return CropBuilder
     */
    fun cropSize(cropWidth : Int,cropHeight : Int):CropBuilder{
        this.cropWidth = cropWidth
        this.cropHeight = cropHeight
        return this
    }

    /**
     * int afterCropFile path
     * @param afterCropFile File
     * @return CropBuilder
     */
    fun afterCropFile(afterCropFile: File):CropBuilder{
        this.afterCropFile = afterCropFile
        return this
    }

    override fun getParamsBuilder(): CropBuilder {
        return this
    }

    override fun generateWorker(builder: CropBuilder): Worker<CropBuilder, CropResult> {
        return CropWorker(functionManager.container,builder)
    }

}