package com.qw.photo.callback


/**
 * @author cd5160866
 */
interface GetImageCallBack<Result> {

    /**
     * 当调用 applyWithDispose 才会回调
     * 图片处理开始
     */
    fun onDisposeStart() {
    }

    /**
     * 整体操作成功
     */
    fun  onSuccess(data: Result)

    /**
     * 失败
     * @param exception 任意一步发生异常
     */
    fun onFailed(exception: Exception)

    /**
     * 取消
     */
    fun onCancel() {
    }
}