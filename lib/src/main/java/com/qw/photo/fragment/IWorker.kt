package com.qw.photo.fragment

import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.BaseParams


/**
 *
 * @author cd5160866
 */
interface IWorker {

    /**
     * 行为
     */
    fun setActions(action: Action)

    /**
     * 设置参数
     */
    fun setParams(params: BaseParams)

    /**
     * 开始
     */
    fun start(callBack: BaseCallBack)

}