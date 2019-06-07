package com.qw.photo

import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.fragment.IWorker


/**
 *
 * @author cd5160866
 */
class Executor<Result>(private val mWorker: IWorker<Result>) {

    fun start(callBack: GetImageCallBack<Result>) {
        mWorker.start(callBack)
    }
}