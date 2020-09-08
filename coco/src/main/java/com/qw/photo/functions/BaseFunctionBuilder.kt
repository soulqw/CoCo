package com.qw.photo.functions

import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.BaseResult
import com.qw.photo.work.Executor
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.IWorker
import com.qw.photo.work.Worker
import java.io.File

/**
 * @author cd5160866
 */
abstract class BaseFunctionBuilder<Result : BaseResult>(
    internal val functionManager: FunctionManager) {

    internal var file: File? = null

    fun then(): FunctionManager {
        this.functionManager.workerFlows.add(createWorker())
        return this.functionManager
    }

    /**
     * 应用参数为后续操作做准备
     * （不会做额外处理）
     */
    fun apply(): Executor<Result> {
        return Executor(this)
    }

    internal abstract fun createWorker(): Worker

    /**
     * 应用参数为后续操作做准备，并根据ImageDisposer 做图片处理
     */
    @JvmOverloads
    fun applyWithDispose(compressor: ImageDisposer = DefaultImageDisposer.getDefault()): Executor<Result> {
        return apply()
    }
}