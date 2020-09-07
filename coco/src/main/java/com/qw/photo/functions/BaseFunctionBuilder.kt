package com.qw.photo.functions

import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.BaseResult
import com.qw.photo.work.Executor
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.IWorker
import java.io.File

/**
 * @author cd5160866
 */
open class BaseFunctionBuilder<Result : BaseResult>(
    private val functionManager: FunctionManager,
    internal val worker: IWorker<*, Result>
) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    fun then(): FunctionManager {
        return this.functionManager
    }

    /**
     * 应用参数为后续操作做准备
     * （不会做额外处理）
     */
    fun apply(): Executor<Result> {
        return Executor(this)
    }

    /**
     * 应用参数为后续操作做准备，并根据ImageDisposer 做图片处理
     */
    @JvmOverloads
    fun applyWithDispose(compressor: ImageDisposer = DefaultImageDisposer.getDefault()): Executor<Result> {
        this.disposer = compressor
        return apply()
    }
}