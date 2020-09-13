package com.qw.photo.functions

import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.Worker

/**
 * @author cd5160866
 */
abstract class BaseFunctionBuilder<Builder, Result>(
    internal val functionManager: FunctionManager
) {

    fun then(): FunctionManager {
        this.functionManager.workerFlows.add(generateWorker(getParamsBuilder()))
        return this.functionManager
    }

    /**
     * 开始
     */
    fun start(callback: CoCoCallBack<Result>) {
        synchronized(functionManager) {
            this.functionManager.workerFlows.add(generateWorker(getParamsBuilder()))
            val iterator = functionManager.workerFlows.iterator()
            if (!iterator.hasNext()) {
                return
            }
            realApply(null, iterator, callback)
        }
    }

    private fun realApply(
        formerResult: Any?,
        iterator: MutableIterator<Any>,
        callback: CoCoCallBack<Result>
    ) {
        val worker: Worker<Builder, Result> = iterator.next() as Worker<Builder, Result>
        worker.start(formerResult, object : CoCoCallBack<Result> {

            override fun onSuccess(data: Result) {
                if (iterator.hasNext()) {
                    iterator.remove()
                    realApply(data, iterator, callback)
                } else {
                    callback.onSuccess(data)
                }
            }

            override fun onFailed(exception: Exception) {
                callback.onFailed(exception)
            }
        })
    }

    internal abstract fun getParamsBuilder(): Builder

    internal abstract fun generateWorker(builder: Builder): Worker<Builder, Result>

}