package com.qw.photo.callback


/**
 * @author cd5160866
 */
interface CoCoCallBack<ResultData> {

    /**
     * get final result data
     * @param data CoCo.with.xxx.then.xxx.start ->  result data
     */
    fun onSuccess(data: ResultData)

    /**
     * 失败
     * @param exception 任意位置发生异常
     */
    fun onFailed(exception: Exception)

}