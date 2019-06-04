package com.qw.photo.callback

import com.qw.photo.pojo.ResultData


/**
 * @author cd5160866
 */
interface BaseCallBack {

    /**
     * 图片处理开始
     */
    fun onDisposeStart() {
    }

    /**
     * 整体操作成功
     */
    fun onSuccess(data: ResultData)

    /**
     * 取消
     */
    fun onCancel(){
    }

    /**
     * 失败
     * @param exception 任意一部发生异常
     */
    fun onFailed(exception: Exception)

}