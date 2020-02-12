package com.qw.photo.exception

import com.qw.photo.DevUtil
import com.qw.photo.constant.Constant

/**
 * @author cd5160866
 */
open class BaseException(override val message: String) : IllegalStateException() {
    init {
        DevUtil.d(Constant.TAG, message)
    }
}

class CompressFailedException(override val message: String) : BaseException(message)

class MissCompressStrategyException : BaseException("compress image must have a strategy")

class PickNoResultException : BaseException("try to get local image with no result")

class MissPermissionException :
    BaseException("Miss permission group in Manifest.permission_group.STORAGE, you may request then before take or pick photos")

class ActivityStatusException :
    BaseException("activity is destroyed or in a error status check your current activity status before use coco")