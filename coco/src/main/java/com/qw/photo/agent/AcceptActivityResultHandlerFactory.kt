package com.qw.photo.agent

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


/**
 * Created by rocket on 2019/6/18.
 */
internal class AcceptActivityResultHandlerFactory {
    companion object {
        private const val TAG = "AcceptResultFragment"

        fun create(activity: Activity): IAcceptActivityResultHandler {
            return if (activity is FragmentActivity) {
                activity.supportFragmentManager.run {
                    val resultFragment = findFragmentByTag(TAG) as? AcceptResultSupportFragment?
                    resultFragment ?: getAcceptResultSupportFragment(this)
                }
            } else {
                activity.fragmentManager.run {
                    val resultFragment = findFragmentByTag(TAG) as? AcceptResultFragment?
                    resultFragment ?: getAcceptResultFragment(this)
                }
            }
        }

        fun create(fragment: Fragment): IAcceptActivityResultHandler {
            val resultFragment =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? AcceptResultSupportFragment?
            return resultFragment ?: getAcceptResultSupportFragment(fragment.childFragmentManager)
        }

        fun create(fragment: android.app.Fragment): IAcceptActivityResultHandler {
            val fm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fragment.childFragmentManager
            } else {
                fragment.fragmentManager
            }
            val resultFragment = fm.findFragmentByTag(TAG) as? AcceptResultFragment?
            return resultFragment ?: getAcceptResultFragment(fm)
        }

        private fun getAcceptResultSupportFragment(fm: FragmentManager): AcceptResultSupportFragment {
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