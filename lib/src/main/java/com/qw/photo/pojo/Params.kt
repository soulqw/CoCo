package com.qw.photo.pojo

import android.util.Log
import com.qw.photo.Executor
import com.qw.photo.fragment.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams(private val worker: IWorker) {

    fun applyWithCompress(): Executor {
        return apply()
    }

    fun apply(): Executor {
        return Executor(worker)
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

class PickParams(worker: IWorker) : BaseParams(worker) {

}