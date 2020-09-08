package com.qw.photo.agent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.callback.Host
import com.qw.photo.constant.Constant

/**
 * Created by rocket on 2019/6/18.
 */
class AcceptResultSupportFragment : Fragment(), IContainer, Host {

    private var current = Host.Status.INIT

    private var requestCode: Int = 0

    private var callback: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null

    override fun startActivityResult(
        intent: Intent, requestCode: Int,
        callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
    ) {
        this.requestCode = requestCode
        this.callback = callback
        startActivityForResult(intent, requestCode)
    }

    override fun provideActivity(): Activity? = activity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == callback || this.requestCode != requestCode) {
            return
        }
        if (!Utils.isHostAvailable(this)) {
            DevUtil.e(Constant.TAG, " already dead ,did not callback")
            //  host已经销毁
            return
        }
        callback!!(requestCode, resultCode, data)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        current = Host.Status.LIVE
    }

    override fun onDestroy() {
        current = Host.Status.DEAD
        super.onDestroy()
        callback = null
    }

    override fun getStatus(): Int {
        return current
    }

    override fun getLifecycleHost(): Host {
        return this
    }

}