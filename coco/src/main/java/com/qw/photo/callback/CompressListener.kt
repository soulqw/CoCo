package com.qw.photo.callback

import com.qw.photo.pojo.DisposeResult


/**
 *
 * @author cd5160866
 */
internal interface CompressListener {

    /**
     * start compress pic
     */
    fun onStart(path: String)

    /**
     * when the dispose finished
     */
    fun onFinish(disposeResult: DisposeResult)

    /**
     * when the error
     */
    fun onError(e: Exception)

}