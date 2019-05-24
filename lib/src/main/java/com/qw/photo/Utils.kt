package com.qw.photo


/**
 *
 * @author cd5160866
 */
object Utils {

    /**
     * 是否是约定好的requestCode
     */
    fun isDefinedRequestCode(requestCode: Int): Boolean {
        val inner = Constant.REQUEST_CODE_IMAGE_CAPTURE or
                Constant.REQUEST_CODE_IMAGE_PICK
        return (inner and requestCode) != 0
    }
}