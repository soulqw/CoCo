package com.qw.photo.work

import com.qw.photo.agent.IContainer
import com.qw.photo.functions.BaseFunctionBuilder
import com.qw.photo.pojo.BaseResult

/**
 * Created by rocket on 2019/6/18.
 */
abstract class BaseWorker<Params : BaseFunctionBuilder<Result>, Result : BaseResult>(val mHandler: IContainer) :
    IWorker<Params, Result> {

    lateinit var mParams: Params

    override fun setParams(params: Params) {
        this.mParams = params
    }
}