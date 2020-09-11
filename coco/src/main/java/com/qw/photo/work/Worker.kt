package com.qw.photo.work

import com.qw.photo.callback.CoCoCallBack

interface Worker<ResultData> {

    fun start(callBack: CoCoCallBack<ResultData>)

}