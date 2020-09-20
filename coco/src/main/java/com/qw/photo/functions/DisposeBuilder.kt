package com.qw.photo.functions

import com.qw.photo.callback.DisposeCallBack
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.Disposer
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

    lateinit var disposer: Disposer

    internal var disposeCallBack: DisposeCallBack? = null

    /**
     * Set the origin file path to dispose, if dispose work after other operate such as
     * @see FunctionManager.pick()
     * or
     * @see FunctionManager.take()
     * the origin path will be set from former result automatic
     *
     * @param originPath the origin path to dispose
     */
    fun origin(originPath: String): DisposeBuilder {
        this.originPath = originPath
        return this
    }

    /**
     * Set the origin file  to dispose, if dispose work after other operate such as
     * @see FunctionManager.pick()
     * or
     * @see FunctionManager.take()
     * the origin path will be set from former result automatic
     *
     * @param originFile the origin File to dispose
     */
    fun origin(originFile: File): DisposeBuilder {
        return origin(originFile.absolutePath)
    }

    /**
     * Set the file that save the result of dispose ,
     * if not set, the result of dispose will not be save
     *
     * if dispose work after other operate
     * @see FunctionManager.pick()
     * the  targetFile will be set from former result automatic
     *
     * @param file the dispose result to save
     */
    fun fileToSaveResult(file: File): DisposeBuilder {
        this.targetFile = file
        return this
    }

    /**
     * Set the disposer to dispose file work on background thread
     *
     * @param disposer how to dispose the file : compress, rotation and so on
     * @see DefaultImageDisposer  the default imp of ImageDisposer .it can compress,rotation the image
     *  @see Disposer  the interface that you can also custom
     */
    fun disposer(disposer: Disposer): DisposeBuilder {
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