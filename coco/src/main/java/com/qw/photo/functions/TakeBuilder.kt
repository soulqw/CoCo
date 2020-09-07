package com.qw.photo.functions

import com.qw.photo.DevUtil
import com.qw.photo.annotations.CameraFace
import com.qw.photo.constant.Constant
import com.qw.photo.pojo.TakeResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.IWorker
import java.io.File

class TakeBuilder(fm: FunctionManager, worker: IWorker<TakeBuilder, TakeResult>) :
    BaseFunctionBuilder<TakeResult>(fm, worker) {

    companion object {

        const val FRONT = 1

        const val BACK = 0

    }

    internal var cameraFace = BACK

    /**
     * 指定被最终写到的文件
     */
    fun targetFile(file: File): TakeBuilder {
        DevUtil.d(Constant.TAG, "capture: saveFilePath: " + (file.path ?: "originUri is null"))
        this.file = file
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
}