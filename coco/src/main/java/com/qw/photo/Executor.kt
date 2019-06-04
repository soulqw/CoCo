package com.qw.photo

import com.qw.photo.callback.BaseCallBack
import com.qw.photo.fragment.IWorker


/**
 *
 * @author cd5160866
 */
class Executor(private val mWorker: IWorker) {

    fun start(callBack: BaseCallBack) {
        mWorker.start(callBack)
    }
}