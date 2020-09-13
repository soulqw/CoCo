package com.qw.photo.functions

import com.qw.photo.callback.DisposeCallBack
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.work.DisposeWorker
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.Worker
import java.io.File

/**
 *Author: 思忆
 *Date: Created in 2020/9/8 3:06 PM
 */
class DisposeBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<DisposeBuilder, DisposeResult>(fm) {

    internal lateinit var targetFile: File

    internal var originPath: String? = null

    internal var disposer: ImageDisposer = DefaultImageDisposer.getDefault()

    internal var disposeCallBack: DisposeCallBack? = null

    fun origin(originPath: String): DisposeBuilder {
        this.originPath = originPath
        return this
    }

    fun target(file: File): DisposeBuilder {
        this.targetFile = file
        return this
    }

    fun disposer(disposer: ImageDisposer): DisposeBuilder {
        this.disposer = disposer
        return this
    }

    fun callBack(callBack: DisposeCallBack): DisposeBuilder {
        this.disposeCallBack = callBack
        return this
    }

    override fun getParamsBuilder(): DisposeBuilder {
        return this
    }

    override fun generateWorker(builder: DisposeBuilder): Worker<DisposeBuilder, DisposeResult> {
        return DisposeWorker(functionManager.container, builder)
    }

}