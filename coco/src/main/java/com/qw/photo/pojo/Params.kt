package com.qw.photo.pojo

import android.util.Log
import com.qw.photo.Executor
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.fragment.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams<Result>(private val worker: IWorker<Result>) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    fun apply(): Executor<Result> {
        return Executor(worker)
    }

    fun applyWithDispose(compressor: ImageDisposer = ImageDisposer.getDefault()): Executor<Result> {
        this.disposer = compressor
        return apply()
    }
}

class CaptureParams<Result>(worker: IWorker<Result>) : BaseParams<Result>(worker) {

    fun targetFile(file: File?): CaptureParams<Result> {
        Log.d("qw", "capture: saveFilePath: " + (file?.path ?: "uri is null"))
        this.file = file
        return this
    }
}

class PickParams<Result>(worker: IWorker<Result>) : BaseParams<Result>(worker) {

    fun targetFile(file: File?): PickParams<Result> {
        Log.d("qw", "pick: saveFilePath: " + (file?.path ?: "uri is null"))
        this.file = file
        return this
    }
}