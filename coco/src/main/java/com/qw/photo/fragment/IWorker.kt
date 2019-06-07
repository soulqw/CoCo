package com.qw.photo.fragment

import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Action
import com.qw.photo.pojo.BaseParams


/**
 *
 * @author cd5160866
 */
interface IWorker<Result> {

    /**
     * 行为
     */
    fun setActions(action: Action)

    /**
     * 设置参数
     */
    fun setParams(params: BaseParams<Result>)

    /**
     * 开始
     */
    fun start(callBack: GetImageCallBack<Result>)

}