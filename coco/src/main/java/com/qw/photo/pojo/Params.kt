package com.qw.photo.pojo

import com.qw.photo.DevUtil
import com.qw.photo.Executor
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.work.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams(internal val worker: IWorker<*,*>) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    /**
     * 应用参数为后续操作做准备
     * （不会压缩）
     */
    fun apply(): Executor {
        return Executor(this)
    }

    /**
     * 应用参数为后续操作做准备，并且可自定义压缩策略
     */
    @JvmOverloads
    fun applyWithDispose(compressor: ImageDisposer = ImageDisposer.getDefault()): Executor {
        this.disposer = compressor
        return apply()
    }
}

class TakeParams(worker: IWorker<TakeParams, TakeResult>) : BaseParams(worker) {

    /**
     * 指定被最终写到的文件
     */
    fun targetFile(file: File): TakeParams {
        DevUtil.d(Constant.TAG, "capture: saveFilePath: " + (file.path ?: "originUri is null"))
        this.file = file
        return this
    }
}

class PickParams(worker: IWorker<PickParams, PickResult>) : BaseParams(worker) {

    fun targetFile(file: File?): PickParams {
        DevUtil.d(Constant.TAG, "pick: saveFilePath: " + (file?.path ?: "originUri is null"))
        this.file = file
        return this
    }
}