package com.qw.photo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.qw.photo.exception.ActivityStatusException


/**
 *
 * @author cd5160866
 */

object CoCo {

    @JvmStatic
    fun with(activity: FragmentActivity): FunctionManager {
        return FunctionManager(activity)
    }

    @JvmStatic
    fun with(fragment: Fragment): FunctionManager {
        val activity = fragment.activity
        if (!Utils.isActivityAvailable(activity)) {
            throw ActivityStatusException()
        }
        return FunctionManager(activity!!)
    }

    @JvmStatic
    fun setDebug(isDebug: Boolean) {
        DevUtil.isDebug = isDebug
    }
}
