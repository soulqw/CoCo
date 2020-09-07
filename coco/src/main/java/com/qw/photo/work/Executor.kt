package com.qw.photo.work

import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.functions.BaseFunctionBuilder
import com.qw.photo.pojo.BaseResult


/**
 *
 * @author cd5160866
 */
class Executor<Result : BaseResult>(private val functionBuilder: BaseFunctionBuilder<Result>) {

    fun start(callBack: GetImageCallBack<Result>) {
        (functionBuilder.worker as IWorker<BaseFunctionBuilder<Result>, Result>).apply {
            this.setParams(functionBuilder)
            this.start(callBack)
        }
    }
}