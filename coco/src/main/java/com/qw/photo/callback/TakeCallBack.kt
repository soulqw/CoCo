package com.qw.photo.callback

import com.qw.photo.pojo.TakeResult

/**
 *Author: 思忆
 *Date: Created in 2020/9/12 4:29 PM
 */
interface TakeCallBack : BaseFunctionCallBack {

    /**
     * do when get the take result
     */
    fun onFinish(result: TakeResult)

    /**
     * do when take canceled
     */
    fun onCancel()

}