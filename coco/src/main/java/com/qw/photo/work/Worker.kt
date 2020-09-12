package com.qw.photo.work

import com.qw.photo.callback.CoCoCallBack

interface Worker<Builder, ResultData> {

    fun start(params: Builder, callBack: CoCoCallBack<ResultData>)

}