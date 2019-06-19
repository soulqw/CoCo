package com.qw.photo

import android.app.Activity
import android.app.Fragment
import com.qw.photo.exception.ActivityStatusException
import com.qw.photo.work.WorkManager


/**
 * @author cd5160866
 */

object CoCo {

    @JvmStatic
    fun with(activity: Activity): WorkManager {
        checkStatusFirst(activity)
        return WorkManager.create(activity)
    }

    @JvmStatic
    fun with(fragment: android.support.v4.app.Fragment): WorkManager {
        checkStatusFirst(fragment.activity)
        return WorkManager.create(fragment)
    }

    @JvmStatic
    fun with(fragment: Fragment): WorkManager {
        checkStatusFirst(fragment.activity)
        return WorkManager.create(fragment)
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
