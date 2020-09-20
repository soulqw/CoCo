package com.qw.photo.functions

import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.dispose.WorkThread
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.Worker

/**
 * @author cd5160866
 */
abstract class BaseFunctionBuilder<Builder, Result>(
    internal val functionManager: FunctionManager
) {

    /**
     * make you can convert to other operate to combine other functions
     * take().then().dispose()....
     */
    fun then(): FunctionManager {
        this.functionManager.workerFlows.add(generateWorker(getParamsBuilder()))
        return this.functionManager
    }

    /**
     * call start to begin the workflow
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
                    //final release
                    WorkThread.release()
                }
            }

            override fun onFailed(exception: Exception) {
                callback.onFailed(exception)
                //final release
                WorkThread.release()
            }
        })
    }

    internal abstract fun getParamsBuilder(): Builder

    internal abstract fun generateWorker(builder: Builder): Worker<Builder, Result>

}