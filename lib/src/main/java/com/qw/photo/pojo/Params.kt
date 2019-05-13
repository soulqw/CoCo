package com.qw.photo.pojo

import com.qw.photo.RequestManager


/**
 *
 * @author cd5160866
 */
open class BaseParams(private val manager: RequestManager) {
    fun apply(): RequestManager {
        return manager
    }
}

class CaptureParams(manager: RequestManager) : BaseParams(manager) {

}

class PickParams(manager: RequestManager) : BaseParams(manager) {

}