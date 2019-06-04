package com.qw.photo.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import com.qw.photo.Utils
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.callback.CompressListener
import com.qw.photo.constant.Action
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.PickParams
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
                val params = mParam
                if (null != targetFile) {
                    result.targetFile = targetFile
                    val targetPath = result.targetFile!!.absolutePath
                    //判断当前状态是否需要处理
                    if (!TextUtils.isEmpty(targetPath) && null != params.disposer) {
                        disposeImage(targetPath, mParam.file, mParam.disposer!!, result, mCallBack)
                        return
                    }
                }
                mCallBack.onSuccess(result)
            }
            Constant.REQUEST_CODE_IMAGE_PICK -> {
                val result = ResultData()
                if (null != data) {
                    result.uri = data.data
                    val params = mParam as PickParams
                    //判断当前状态是否需要处理
                    if (null != data.data && Utils.isActivityAvailable(activity)) {
                        val localPath = Utils.uriToImagePath(activity!!, data.data!!)
                        if (!TextUtils.isEmpty(localPath) && null != params.disposer) {
                            disposeImage(localPath!!, mParam.file, mParam.disposer!!, result, mCallBack)
                            return
                        }
                    }
                    mCallBack.onSuccess(result)
                } else {
                    mCallBack.onFailed(NullPointerException("null result data"))
                }
            }
        }
    }

    /**
     * 处理图片
     * @param originPath 图片原始路径
     * @param targetFile 图片处理之后的保存文件 可为空
     * @param disposer 图片处理器
     * @param resultData 处理完统一封装的实体类
     * @param callBack 回调
     */
    private fun disposeImage(
        originPath: String,
        targetFile: File?,
        disposer: ImageDisposer,
        resultData: ResultData,
        callBack: BaseCallBack
    ) {
        disposer.dispose(originPath, targetFile, object : CompressListener {
            override fun onStart(path: String) {
                callBack.onDisposeStart()
            }

            override fun onFinish(compressed: Bitmap, savedFile: File?) {
                resultData.compressBitmap = compressed
                resultData.targetFile = savedFile
                Log.d("qw", "onSuccess $resultData")
                callBack.onSuccess(resultData)
            }

            override fun onError(e: Exception) {
                callBack.onFailed(e)
            }
        })
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
//        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
//        pickIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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