package com.qw.photo.agent

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.constant.Constant

/**
 * Created by rocket on 2019/6/18.
 */
class AcceptResultFragment : Fragment(), IAcceptActivityResultHandler {

    private var requestCode: Int = 0
    private var callback: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null

    override fun startActivityResult(intent: Intent, requestCode: Int,
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
        if (!Utils.isActivityAvailable(activity)) {
            DevUtil.e(Constant.TAG, "activity already dead ,did not callback")
            //activity 已经销毁
            return
        }
        callback!!(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }
}