package com.qw.photo.functions

import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.Worker
import java.io.File

/**
 * @author cd5160866
 */
abstract class BaseFunctionBuilder<Result>(
    private val functionManager: FunctionManager<Result>,
    internal val worker: Worker<Result>
) {

    internal var file: File? = null

    fun then(): FunctionManager<Result> {
        this.functionManager.workerFlows.add(this)
        return this.functionManager
    }

    /**
     * 应用参数为后续操作做准备
     * （不会做额外处理）
     */
    fun apply(callback: CoCoCallBack<Result>) {
        val iterator = functionManager.workerFlows.iterator()
        iterator.forEach {
            it.worker.start(object : CoCoCallBack<Result> {
                override fun onSuccess(data: Result) {
                    if (iterator.hasNext()) {
                        iterator.remove()
                    } else {
                        callback.onSuccess(data)
                    }
                }

                override fun onFailed(exception: Exception) {
                    callback.onFailed(exception)
                }
            })
        }
    }

}