package com.qw.photo.callback


/**
 * Simple CallBackAdapter
 */
abstract class SimpleGetImageAdapter<Result> : GetImageCallBack<Result> {

    override fun onCancel() {}

    override fun onDisposeStart() {}

    override fun onFailed(exception: Exception) {}

}