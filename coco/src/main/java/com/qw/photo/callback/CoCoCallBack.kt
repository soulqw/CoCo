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
     * failed in any position
     * @param exception
     */
    fun onFailed(exception: Exception)

}