package com.qw.photo

import android.app.Activity
import android.support.v4.app.Fragment
import com.qw.photo.exception.ActivityStatusException


/**
 * @author cd5160866
 */

object CoCo {

    @JvmStatic
    fun with(activity: Activity): FunctionManager {
        return FunctionManager(activity)
    }

    @JvmStatic
    fun with(fragment: Fragment): FunctionManager {
        if (!Utils.isActivityAvailable(fragment.activity)) {
            throw ActivityStatusException()
        }
        return FunctionManager(fragment.activity!!)
    }

    @JvmStatic
    fun setDebug(isDebug: Boolean) {
        DevUtil.isDebug = isDebug
    }
}
