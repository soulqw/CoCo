package com.qw.photo.callback

import com.qw.photo.pojo.BaseResult


/**
 *
 * @author cd5160866
 */
interface CompressListener {

    /**
     * 开始压缩
     */
    fun onStart(path: String)

    /**
     * 任务结束
     */
    fun onFinish(disposeResult: BaseResult)

    /**
     * 发生错误
     */
    fun onError(e: Exception)

}