package com.qw.photo.work

import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.BaseResult


/**
 *
 * @author cd5160866
 */
class Executor<Result : BaseResult>(private val params: BaseParams<Result>) {

    fun start(callBack: GetImageCallBack<Result>) {
        (params.worker as IWorker<BaseParams<Result>, Result>).apply {
            this.setParams(params)
            this.start(callBack)
        }
    }
}