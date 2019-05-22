package com.qw.photo.pojo

import android.net.Uri
import com.qw.photo.Executor
import com.qw.photo.fragment.IWorker


/**
 * @author cd5160866
 */
open class BaseParams(private val worker: IWorker) {

    fun apply(): Executor {
        return Executor(worker)
    }
}

class CaptureParams(worker: IWorker) : BaseParams(worker) {
    internal var uri: Uri? = null

    fun uri(uri: Uri?): CaptureParams {
        this.uri = uri
        return this
    }
}

class PickParams(worker: IWorker) : BaseParams(worker) {

}