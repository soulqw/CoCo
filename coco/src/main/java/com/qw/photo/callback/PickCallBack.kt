package com.qw.photo.callback

import com.qw.photo.pojo.PickResult

/**
 *Author: 思忆
 *Date: Created in 2020/9/12 4:29 PM
 */
interface PickCallBack : BaseFunctionCallBack {

    fun onFinish(result: PickResult)

    fun onCancel()
}