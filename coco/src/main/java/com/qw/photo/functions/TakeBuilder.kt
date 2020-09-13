package com.qw.photo.functions

import com.qw.photo.DevUtil
import com.qw.photo.annotations.CameraFace
import com.qw.photo.callback.TakeCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.pojo.TakeResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.TakePhotoWorker
import com.qw.photo.work.Worker
import java.io.File

class TakeBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<TakeBuilder, TakeResult>(fm) {

    companion object {

        const val FRONT = 1

        const val BACK = 0

    }

    internal var cameraFace = BACK

    internal var fileToSave: File? = null

    internal var takeCallBack: TakeCallBack? = null

    fun callBack(callBack: TakeCallBack): TakeBuilder {
        this.takeCallBack = callBack
        return this
    }

    /**
     * 指定被最终写到的文件
     */
    fun fileToSave(fileToSave: File): TakeBuilder {
        DevUtil.d(
            Constant.TAG,
            "capture: saveFilePath: " + (fileToSave.path ?: "originUri is null")
        )
        this.fileToSave = fileToSave
        return this
    }

    /**
     * 相机的方向，默认后摄像头
     * 这个方法不一定对所有手机生效
     * @see CameraFace
     */
    fun cameraFace(@CameraFace face: Int): TakeBuilder {
        this.cameraFace = face
        return this
    }

    override fun getParamsBuilder(): TakeBuilder {
        return this
    }

    override fun generateWorker(builder: TakeBuilder): Worker<TakeBuilder, TakeResult> {
        return TakePhotoWorker(functionManager.container, builder)
    }
}