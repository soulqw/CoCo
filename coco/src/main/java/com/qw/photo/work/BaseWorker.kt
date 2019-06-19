package com.qw.photo.work

import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.BaseResult

/**
 * Created by rocket on 2019/6/18.
 */
abstract class BaseWorker<Params : BaseParams, Result : BaseResult>(val mHandler: IAcceptActivityResultHandler) :
    IWorker<Params, Result> {

    lateinit var mParams: Params

    override fun setParams(params: Params) {
        this.mParams = params
    }
}