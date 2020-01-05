package com.qw.photo.pojo

import com.qw.photo.DevUtil
import com.qw.photo.annotations.CameraFace
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.work.Executor
import com.qw.photo.work.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams<Result : BaseResult>(internal val worker: IWorker<*, Result>) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    /**
     * 应用参数为后续操作做准备
     * （不会压缩）
     */
    fun apply(): Executor<Result> {
        return Executor(this)
    }

    /**
     * 应用参数为后续操作做准备，并且可自定义压缩策略
     */
    @JvmOverloads
    fun applyWithDispose(compressor: ImageDisposer = DefaultImageDisposer.getDefault()): Executor<Result> {
        this.disposer = compressor
        return apply()
    }
}

class TakeParams(worker: IWorker<TakeParams, TakeResult>) : BaseParams<TakeResult>(worker) {

    companion object {

        const val FRONT = 1

        const val BACK = 0
    }

    var cameraFace = BACK
    /**
     * 指定被最终写到的文件
     */
    fun targetFile(file: File): TakeParams {
        DevUtil.d(Constant.TAG, "capture: saveFilePath: " + (file.path ?: "originUri is null"))
        this.file = file
        return this
    }

    /**
     * 相机的方向，默认后摄像头
     * 这个方法不一定对所有手机生效
     * @see CameraFace
     */
    fun cameraFace(@CameraFace face: Int): TakeParams {
        this.cameraFace = face
        return this
    }
}

class PickParams(worker: IWorker<PickParams, PickResult>) : BaseParams<PickResult>(worker) {

    fun targetFile(file: File?): PickParams {
        DevUtil.d(Constant.TAG, "pick: saveFilePath: " + (file?.path ?: "originUri is null"))
        this.file = file
        return this
    }
}