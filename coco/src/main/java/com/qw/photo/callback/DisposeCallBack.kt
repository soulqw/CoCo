package com.qw.photo.callback

import com.qw.photo.pojo.DisposeResult

interface DisposeCallBack : BaseFunctionCallBack {
    fun onFinish(result: DisposeResult)
}