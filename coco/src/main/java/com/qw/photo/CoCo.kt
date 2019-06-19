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
        checkActivityStatus(activity)
        return WorkManager.create(activity)
    }

    @JvmStatic
    fun with(fragment: android.support.v4.app.Fragment): WorkManager {
        checkActivityStatus(fragment.activity)
        return WorkManager.create(fragment)
    }

    @JvmStatic
    fun with(fragment: Fragment): WorkManager {
        checkActivityStatus(fragment.activity)
        return WorkManager.create(fragment)
    }

    private fun checkActivityStatus(activity: Activity?) {
        if (activity == null || !Utils.isActivityAvailable(activity))
            throw ActivityStatusException()
    }

    @JvmStatic
    fun setDebug(isDebug: Boolean) {
        DevUtil.isDebug = isDebug
    }
}
