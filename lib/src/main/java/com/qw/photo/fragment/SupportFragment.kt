package com.qw.photo.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import com.qw.photo.Constant
import com.qw.photo.Utils
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.ResultData
import java.io.File


/**
 *
 * @author cd5160866
 */
class SupportFragment : Fragment(), IWorker {

    private lateinit var mAction: Action

    private lateinit var mParam: BaseParams

    private lateinit var mCallBack: BaseCallBack

    private var targetFile: File? = null

    override fun setActions(action: Action) {
        this.mAction = action
    }

    override fun setParams(params: BaseParams) {
        this.mParam = params
    }

    override fun start(callBack: BaseCallBack) {
        if (!Utils.isActivityAvailable(activity)) {
            return
        }
        this.mCallBack = callBack
        when (mAction) {
            Action.CAPTURE -> takePhoto()
            else -> {
                pickPhoto()
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
                val result = ResultData()
                if (null != targetFile) {
                    result.file = targetFile
                }
                if (null != data && null != data.extras) {
                    result.thumbnailData = data.extras!!.get("data") as Bitmap
                }
                mCallBack.onSuccess(result)
            }
            Constant.REQUEST_CODE_IMAGE_PICK -> {
                val result = ResultData()
                if (null != data) {
                    result.thumbnailData = data.getParcelableExtra("data")
                }
                mCallBack.onSuccess(result)
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
        targetFile = params.file
        //用户指定了目标文件路径
        if (null != targetFile) {
            try {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Utils.createUriFromFile((this.activity) as Context, params.file!!)
                )
            } catch (e: Exception) {
                Log.e("qw", e.toString())
            }
        }
        try {
            startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_IMAGE_CAPTURE)
        } catch (e: Exception) {
            mCallBack.onFailed(e)
        }
    }

    private fun pickPhoto() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        if (null === pickIntent.resolveActivity(activity!!.packageManager)) {
            mCallBack.onFailed(IllegalStateException("activity status error"))
            return
        }
        try {
            startActivityForResult(pickIntent, Constant.REQUEST_CODE_IMAGE_PICK)
        } catch (e: Exception) {
            mCallBack.onFailed(e)
        }
    }
}