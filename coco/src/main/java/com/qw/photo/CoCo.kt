package com.qw.photo

import android.app.Activity
import android.app.Fragment
import com.qw.photo.exception.ActivityStatusException
import com.qw.photo.work.FunctionManager


/**
 * @author cd5160866
 */

object CoCo {

    @JvmStatic
    fun with(activity: Activity): FunctionManager {
        checkStatusFirst(activity)
        return FunctionManager.create(activity)
    }

    @JvmStatic
    fun with(fragment: androidx.fragment.app.Fragment): FunctionManager {
        checkStatusFirst(fragment.activity)
        return FunctionManager.create(fragment)
    }

    @JvmStatic
    fun with(fragment: Fragment): FunctionManager {
        checkStatusFirst(fragment.activity)
        return FunctionManager.create(fragment)
    }

    @JvmStatic
    fun setDebug(isDebug: Boolean) {
        DevUtil.isDebug = isDebug
    }

    private fun checkStatusFirst(activity: Activity?) {
        Utils.checkNecessaryPermissions(activity)
        if (!Utils.isActivityAvailable(activity)) {
            throw ActivityStatusException()
        }
    }
}
