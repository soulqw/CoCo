package com.qw.photo.callback

abstract class CoCoAdapter<ResultData> : CoCoCallBack<ResultData> {

    override fun onFailed(exception: Exception) {}

    override fun onSuccess(data: ResultData) {}

}