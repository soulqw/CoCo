package com.qw.photo.fragment

import android.app.Activity
import android.support.v4.app.FragmentActivity
import com.qw.photo.pojo.BaseResultData


/**
 * @author cd5160866
 */
internal object FragmentFactory {

    private const val FRAGMENT_TAG = "fragment_tag"

    fun <Result : BaseResultData> create(activity: Activity): IWorker<Result> {
        val action: IWorker<Result>
        val supportFragmentManager = (activity as FragmentActivity).supportFragmentManager
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
}
