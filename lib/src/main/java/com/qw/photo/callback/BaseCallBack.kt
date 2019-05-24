package com.qw.photo.callback

import com.qw.photo.pojo.ResultData


/**
 * @author cd5160866
 */
interface BaseCallBack {

    fun onSuccess(data: ResultData)

    fun onCancel()

    fun onFailed(exception: Exception)

}