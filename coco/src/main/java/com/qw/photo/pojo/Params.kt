package com.qw.photo.pojo

import com.qw.photo.DevUtil
import com.qw.photo.Executor
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.fragment.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams<Result>(private val worker: IWorker<Result>) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    /**
     * 应用参数为后续操作做准备
     * （不会压缩）
     */
    fun apply(): Executor<Result> {
        return Executor(worker)
    }

    /**
     * 应用参数为后续操作做准备，并且可自定义压缩策略
     */
    fun applyWithDispose(compressor: ImageDisposer = ImageDisposer.getDefault()): Executor<Result> {
        this.disposer = compressor
        return apply()
    }
}

class CaptureParams<Result>(worker: IWorker<Result>) : BaseParams<Result>(worker) {

    /**
     * 指定被最终写到的文件
     */
    fun targetFile(file: File): CaptureParams<Result> {
        DevUtil.d(Constant.TAG, "capture: saveFilePath: " + (file.path ?: "originUri is null"))
        this.file = file
        return this
    }
}

class PickParams<Result>(worker: IWorker<Result>) : BaseParams<Result>(worker) {


    fun targetFile(file: File?): PickParams<Result> {
        DevUtil.d(Constant.TAG, "pick: saveFilePath: " + (file?.path ?: "originUri is null"))
        this.file = file
        return this
    }
}