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
        fun with(activity: Activity): RequestManager {
            return RequestManager(activity)
        }

        @JvmStatic
        fun with(fragment: Fragment): RequestManager {
            val activity = fragment.activity
            if (activity == null || activity.isFinishing) {
                throw IllegalStateException("activity is destroyed ")
            }
            return RequestManager(activity)
        }

    }
}
