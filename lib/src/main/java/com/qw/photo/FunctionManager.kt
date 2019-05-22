package com.qw.photo

import android.app.Activity
import com.qw.photo.fragment.FragmentFactory
import com.qw.photo.fragment.IWorker
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.PickParams


/**
 * @author cd5160866
 */
class FunctionManager(containerActivity: Activity) {

    private val mWorker: IWorker = FragmentFactory.create(containerActivity)

    fun take(): CaptureParams {
        val result = CaptureParams(mWorker)
        mWorker.setParams(result)
        mWorker.setActions(Action.CAPTURE)
        return result
    }

    fun pick(): PickParams {
        val result = PickParams(mWorker)
        mWorker.setParams(result)
        mWorker.setActions(Action.PICK)
        return result
    }

}