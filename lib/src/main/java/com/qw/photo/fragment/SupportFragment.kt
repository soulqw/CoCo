package com.qw.photo.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import com.qw.photo.Constant
import com.qw.photo.Utils
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.ResultData


/**
 *
 * @author cd5160866
 */
class SupportFragment : Fragment(), IWorker {

    private lateinit var mAction: Action

    private lateinit var mParam: BaseParams

    private lateinit var mCallBack: BaseCallBack

    override fun setActions(action: Action) {
        this.mAction = action
    }

    override fun setParams(params: BaseParams) {
        this.mParam = params
    }

    override fun start(callBack: BaseCallBack) {
        this.mCallBack = callBack
        when (mAction) {
            Action.CAPTURE -> takePhoto()
            else -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!Utils.isDefinedRequestCode(requestCode)) {
            return
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            mCallBack.onCancel()
            return
        }
        when (requestCode) {
            Constant.REQUEST_CODE_IMAGE_CAPTURE -> {
                if (data == null || data.extras == null) {
                    mCallBack.onSuccess(ResultData())
                    return
                }
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val result = ResultData()
                result.thumbnailData = imageBitmap
                mCallBack.onSuccess(result)
            }
            Constant.REQUEST_CODE_IMAGE_PICK -> {

            }
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (null === takePictureIntent.resolveActivity(activity!!.packageManager)) {
            mCallBack.onFailed(IllegalStateException("activity status error"))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val params: CaptureParams = mParam as CaptureParams
        val uri = params.uri
        if (null === uri) {
            //todo
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_IMAGE_CAPTURE)
    }

}