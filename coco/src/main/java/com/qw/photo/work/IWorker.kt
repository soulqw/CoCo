package com.qw.photo.work

import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.BaseResult

/**
 * Created by rocket on 2019/6/18.
 */
interface IWorker<Params : BaseParams<Result>, Result : BaseResult> {

    /**
     * 设置参数
     */
    fun setParams(params: Params)

    /**
     * 开始
     */
    fun start(callBack: GetImageCallBack<Result>)
}