package com.qw.photo.callback


/**
 * @author cd5160866
 */
interface CoCoCallBack {

    fun onSuccess(data: Any)

    /**
     * 失败
     * @param exception 任意一步发生异常
     */
    fun onFailed(exception: Exception)

}