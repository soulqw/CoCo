package com.qw.photo.work

import com.qw.photo.callback.CoCoCallBack

interface Worker<Builder, ResultData> {

    fun start(formerResult: Any?, callBack: CoCoCallBack<ResultData>)

}