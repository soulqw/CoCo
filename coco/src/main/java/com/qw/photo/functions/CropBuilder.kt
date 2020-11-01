package com.qw.photo.functions

import com.qw.photo.Utils
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
    BaseFunctionBuilder<CropBuilder, CropResult>(fm) {

    var originFile: File? = null

    var savedResultFile: File? = null

    var cropWidth: Int = Utils.getScreenWidth(fm.container.provideActivity()!!)

    var cropHeight: Int = Utils.getScreenHeight(fm.container.provideActivity()!!)

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
     * @param originFile File? the file you want crop
     * @return CropBuilder
     */
    fun file(originFile: File?): CropBuilder {
        this.originFile = originFile
        return this
    }

    /**
     * init crop size
     * @param cropWidth Int
     * @param cropHeight Int
     * @param cropWidth Int Int the crop width and height you want
     * @param cropHeight Int Int the crop width and height you want
     * @return CropBuilder
     */
    fun cropSize(cropWidth: Int, cropHeight: Int): CropBuilder {
        this.cropWidth = cropWidth
        this.cropHeight = cropHeight
        return this
    }

    /**
     * if not set , coco will create an temple file to save the crop result
     * @param fileToSaveResult File
     * @return CropBuilder
     */
    fun fileToSaveResult(fileToSaveResult: File): CropBuilder {
        this.savedResultFile = fileToSaveResult
        return this
    }

    override fun getParamsBuilder(): CropBuilder {
        return this
    }

    override fun generateWorker(builder: CropBuilder): Worker<CropBuilder, CropResult> {
        return CropWorker(functionManager.container, builder)
    }

}