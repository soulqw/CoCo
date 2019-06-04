package com.qw.photo.pojo

import android.util.Log
import com.qw.photo.Executor
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.fragment.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams(private val worker: IWorker) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    fun apply(): Executor {
        return Executor(worker)
    }

    fun applyWithDispose(compressor: ImageDisposer = ImageDisposer.getDefault()): Executor {
        this.disposer = compressor
        return apply()
    }
}

class CaptureParams(worker: IWorker) : BaseParams(worker) {

    fun targetFile(file: File?): CaptureParams {
        Log.d("qw", "capture: saveFilePath: " + (file?.path ?: "uri is null"))
        this.file = file
        return this
    }
}

class PickParams(worker: IWorker) : BaseParams(worker) {

    fun targetFile(file: File?): PickParams {
        Log.d("qw", "pick: saveFilePath: " + (file?.path ?: "uri is null"))
        this.file = file
        return this
    }
}