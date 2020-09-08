package com.qw.photo.functions

import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.IWorker

/**
 *Author: 思忆
 *Date: Created in 2020/9/8 3:06 PM
 */
class DisposeBuilder(fm: FunctionManager, worker: IWorker<DisposeBuilder, DisposeResult>) :
    BaseFunctionBuilder<DisposeResult>(fm, worker) {

    fun target() {}

    fun disposer(disposer: ImageDisposer = DefaultImageDisposer.getDefault()) {}

    fun callBack() {}

}