package com.qw.photo

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File


/**
 *
 * @author cd5160866
 */
object Utils {

    /**
     * 是否是约定好的requestCode
     */
    internal fun isDefinedRequestCode(requestCode: Int): Boolean {
        val inner = Constant.REQUEST_CODE_IMAGE_CAPTURE or
                Constant.REQUEST_CODE_IMAGE_PICK
        return (inner and requestCode) != 0
    }

    internal fun createUriFromFile(context: Context, file: File): Uri? {
        if (!file.exists()) {
            file.mkdir()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.applicationInfo.packageName}.coco.provider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    internal fun isActivityAvailable(activity: Activity?): Boolean {
        if (null == activity) {
            return false
        }
        if (activity.isFinishing) {
            Log.d("qw", " activity is finishing :" + activity.javaClass.simpleName)
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
            Log.d("qw", " activity is destroyed :" + activity.javaClass.simpleName)
            return false
        }
        return true
    }
}