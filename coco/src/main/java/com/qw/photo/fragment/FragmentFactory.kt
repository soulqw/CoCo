package com.qw.photo.fragment

import android.app.Activity
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.qw.photo.pojo.BaseResultData


/**
 * @author cd5160866
 */
internal object FragmentFactory {

    private const val FRAGMENT_TAG = "fragment_tag"

    fun <Result : BaseResultData> create(activity: Activity): IWorker<Result> {
        val action: IWorker<Result>
        val supportFragmentManager = getSupportFragmentManager(activity as FragmentActivity)
        var supportFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportFragment<Result>?
        if (null == supportFragment) {
            supportFragment = SupportFragment()
            supportFragmentManager.beginTransaction()
                .add(supportFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss()
        }
        action = supportFragment
        return action
    }

    private fun getSupportFragmentManager(activity: FragmentActivity): FragmentManager {
        val fragmentManager = activity.supportFragmentManager
        return if (fragmentManager.fragments.size > 0 && null != fragmentManager.fragments[0]) {
            fragmentManager.fragments[0].childFragmentManager
        } else fragmentManager
    }
}
