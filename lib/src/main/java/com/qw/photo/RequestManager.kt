package com.qw.photo

import android.app.Activity
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.fragment.FragmentFactory
import com.qw.photo.fragment.IWorker
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.PickParams


/**
 * @author cd5160866
 */
class RequestManager(containerActivity: Activity) {

    private val mWorker: IWorker = FragmentFactory.create(containerActivity)

    fun take(): CaptureParams {
        val result = CaptureParams(this)
        mWorker.setActions(Action.CAPTURE)
        mWorker.setParams(result)
        return result
    }

    fun pick(): PickParams {
        val result = PickParams(this)
        mWorker.setActions(Action.PICK)
        mWorker.setParams(result)
        return result
    }

    fun start(callBack: BaseCallBack) {
        mWorker.start(callBack)
    }
}