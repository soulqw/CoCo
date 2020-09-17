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

    internal var targetFile: File? = null

    internal var originPath: String? = null

    lateinit var disposer: ImageDisposer

    internal var disposeCallBack: DisposeCallBack? = null

    /**
     * set the origin file path to dispose, if dispose work after other operate such as
     * @see FunctionManager.pick()
     * or
     * @see FunctionManager.take()
     * the origin path will set from former result automatic
     *
     * @param originPath the origin path to dispose
     */
    fun origin(originPath: String): DisposeBuilder {
        this.originPath = originPath
        return this
    }

    /**
     * @param file the dispose result to save
     */
    fun target(file: File): DisposeBuilder {
        this.targetFile = file
        return this
    }

    /**
     *
     * @param disposer how to dispose image ,compress, rotation and so on
     * @see ImageDisposer  the interface that you can custom
     * @see DefaultImageDisposer  the default imp of ImageDisposer .it can compress,rotation the image
     */
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