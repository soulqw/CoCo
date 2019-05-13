package com.qw.photo.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.ResultData


/**
 *
 * @author cd5160866
 */
class SupportFragment : Fragment(), IWorker {

    lateinit var mAction: Action

    lateinit var mParam: BaseParams

    lateinit var mCallBack: BaseCallBack

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
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE_CAPTURE -> {
                data?.extras ?: return
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val result = ResultData()
                result.thumbnailData = imageBitmap
                mCallBack.onSuccess(result)
            }
            REQUEST_CODE_IMAGE_PICK -> {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {

        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent: Intent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
            }
        }
    }

    companion object {

        private const val REQUEST_CODE_IMAGE_CAPTURE = 1

        private const val REQUEST_CODE_IMAGE_PICK = 2

        private const val PERMISSION_CODE = 11

    }
}