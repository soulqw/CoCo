package com.qw.photo

import android.app.Activity
import android.support.v4.app.Fragment


/**
 *
 * @author cd5160866
 */
class CoCo {

    companion object {

        @JvmStatic
        fun with(activity: Activity): FunctionManager {
            return FunctionManager(activity)
        }

        @JvmStatic
        fun with(fragment: Fragment): FunctionManager {
            val activity = fragment.activity
            if (activity == null || activity.isFinishing) {
                throw IllegalStateException("activity is destroyed ")
            }
            return FunctionManager(activity)
        }

        @Suppress("DEPRECATION")
        @JvmStatic
        fun with(fragment: android.app.Fragment): FunctionManager {
            val activity = fragment.activity
            if (activity == null || activity.isFinishing) {
                throw IllegalStateException("activity is destroyed ")
            }
            return FunctionManager(activity)
        }
    }
}
