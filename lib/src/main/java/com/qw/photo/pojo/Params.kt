package com.qw.photo.pojo

import android.util.Log
import com.qw.photo.Executor
import com.qw.photo.compress.CompressHelper
import com.qw.photo.fragment.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams(private val worker: IWorker) {

    var compressor: CompressHelper? = null

    fun apply(): Executor {
        return Executor(worker)
    }

    fun applyWithCompress(compressor: CompressHelper = CompressHelper.getDefault()): Executor {
        this.compressor = compressor
        return apply()
    }
}

class CaptureParams(worker: IWorker) : BaseParams(worker) {

    internal var file: File? = null

    fun targetFile(file: File?): CaptureParams {
        Log.d("qw", "FilePath: " + (file?.path ?: "uri is null"))
        this.file = file
        return this
    }
}

class PickParams(worker: IWorker) : BaseParams(worker)