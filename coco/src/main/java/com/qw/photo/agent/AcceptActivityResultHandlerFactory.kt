package com.qw.photo.agent

import android.app.Activity
import android.app.Fragment
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager


/**
 * Created by rocket on 2019/6/18.
 */
internal class AcceptActivityResultHandlerFactory {
    companion object {
        private const val TAG = "com.qw.photo.agent.AcceptResultFragment"

        fun create(activity: Activity): IAcceptActivityResultHandler {
            return if (activity is FragmentActivity) {
                activity.supportFragmentManager.run {
                    val resultFragment = findFragmentByTag(TAG) as? AcceptResultSupportFragment?
                    resultFragment ?: getAcceptResultFragmentV4(this)
                }
            } else {
                activity.fragmentManager.run {
                    val resultFragment = findFragmentByTag(TAG) as? AcceptResultFragment?
                    resultFragment ?: getAcceptResultFragment(this)
                }
            }
        }

        fun create(fragment: android.support.v4.app.Fragment): IAcceptActivityResultHandler {
            val resultFragment = fragment.childFragmentManager.findFragmentByTag(TAG) as? AcceptResultSupportFragment?
            return resultFragment ?: getAcceptResultFragmentV4(fragment.childFragmentManager)
        }

        fun create(fragment: Fragment): IAcceptActivityResultHandler {
            val fm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fragment.childFragmentManager
            } else {
                fragment.fragmentManager
            }
            val resultFragment = fm.findFragmentByTag(TAG) as? AcceptResultFragment?
            return resultFragment ?: getAcceptResultFragment(fm)
        }

        private fun getAcceptResultFragmentV4(fm: FragmentManager): AcceptResultSupportFragment {
            val fragment = AcceptResultSupportFragment()
            fm.beginTransaction()
                    .add(fragment, TAG)
                    .commitNowAllowingStateLoss()
            return fragment
        }

        private fun getAcceptResultFragment(fm: android.app.FragmentManager): AcceptResultFragment {
            val fragment = AcceptResultFragment()
            fm.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss()
            // makes it like commitNow
            fm.executePendingTransactions()
            return fragment
        }
    }
}