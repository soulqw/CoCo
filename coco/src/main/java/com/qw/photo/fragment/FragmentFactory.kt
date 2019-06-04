package com.qw.photo.fragment

import android.app.Activity
import android.support.v4.app.FragmentActivity


/**
 * @author cd5160866
 */
internal object FragmentFactory {

    private const val FRAGMENT_TAG = "fragment_tag"

    fun create(activity: Activity): IWorker {
        val action: IWorker
        val supportFragmentManager = (activity as FragmentActivity).supportFragmentManager
        var supportFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as SupportFragment?
        if (null == supportFragment) {
            supportFragment = SupportFragment()
            supportFragmentManager.beginTransaction()
                .add(supportFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss()
        }
        action = supportFragment
        return action
    }
}
