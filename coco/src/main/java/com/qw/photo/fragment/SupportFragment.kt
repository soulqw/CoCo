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
import com.qw.photo.DevUtil
import com.qw.photo.Utils
import com.qw.photo.callback.CompressListener
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.dispose.WorkThread
import com.qw.photo.exception.ActivityStatusException
import com.qw.photo.exception.BaseException
import com.qw.photo.pojo.BaseParams
import com.qw.photo.pojo.BaseResultData
import com.qw.photo.pojo.CaptureResult
import com.qw.photo.pojo.PickResult
import java.io.File

/**
 *
 * @author cd5160866
 */
class SupportFragment<Result : BaseResultData> : Fragment(), IWorker<Result> {

    private lateinit var mParam: BaseParams<Result>

    private lateinit var mResult: Result

    private lateinit var mCallBack: GetImageCallBack<Result>

    private var targetFile: File? = null

    override fun setParams(params: BaseParams<Result>) {
        this.mParam = params
    }

    override fun setResult(result: Result) {
        this.mResult = result
    }

    override fun start(callBack: GetImageCallBack<Result>) {
        this.mCallBack = callBack
        //check permission first
        Utils.checkNecessaryPermissions(activity)
        //check activity status
        if (!Utils.isActivityAvailable(activity)) {
            mCallBack.onFailed(ActivityStatusException())
            return
        }
        when (mResult) {
            is CaptureResult -> {
                takePhoto()
            }
            else -> {
                pickPhoto()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (!Utils.isDefinedRequestCode(requestCode)) {
            return
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            mCallBack.onCancel()
            return
        }
        when (mResult) {
            is CaptureResult -> {
                if (null != targetFile) {
                    val params = mParam
                    val result = mResult as CaptureResult
                    result.targetFile = targetFile
                    val targetPath = mResult.targetFile!!.absolutePath
                    //判断当前状态是否需要处理
                    if (!TextUtils.isEmpty(targetPath) && null != params.disposer) {
                        disposeImage(targetPath, mParam.file, mParam.disposer!!, mResult, mCallBack)
                        return
                    }
                    mCallBack.onSuccess(mResult)
                }
            }
            is PickResult -> {
                if (null != intentData && null != intentData.data) {
                    val params = mParam
                    val result = mResult as PickResult
                    result.originUri = intentData.data!!
                    //判断当前状态是否需要处理
                    if (null != intentData.data && Utils.isActivityAvailable(activity)) {
                        val localPath = Utils.uriToImagePath(activity!!, intentData.data!!)
                        if (!TextUtils.isEmpty(localPath) && null != params.disposer) {
                            disposeImage(localPath!!, mParam.file, mParam.disposer!!, mResult, mCallBack)
                            return
                        }
                    }
                    mCallBack.onSuccess(mResult)
                } else {
                    mCallBack.onFailed(NullPointerException("null result intentData"))
                }
            }
        }
    }

    /**
     * 拍照的最终方法
     */
    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (null === takePictureIntent.resolveActivity(activity!!.packageManager)) {
            mCallBack.onFailed(BaseException("activity status error"))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val params = mParam
        targetFile = params.file
        //用户指定了目标文件路径
        if (null != targetFile) {
            try {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Utils.createUriFromFile((this.activity) as Context, params.file!!)
                )
            } catch (e: Exception) {
                DevUtil.e(Constant.TAG, e.toString())
            }
        }
        try {
            startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_IMAGE_CAPTURE)
        } catch (e: Exception) {
            mCallBack.onFailed(e)
        }
    }

    /**
     * 选择照片的最终方法
     */
    private fun pickPhoto() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (null === pickIntent.resolveActivity(activity!!.packageManager)) {
            mCallBack.onFailed(BaseException("activity status error"))
            return
        }
        try {
            startActivityForResult(pickIntent, Constant.REQUEST_CODE_IMAGE_PICK)
        } catch (e: Exception) {
            mCallBack.onFailed(e)
        }
    }

    /**
     * 处理图片
     * @param originPath 图片原始路径
     * @param targetFile 图片处理之后的保存文件 可为空
     * @param disposer 图片处理器
     * @param result 结果
     * @param callBack 回调
     */
    private fun disposeImage(
        originPath: String,
        targetFile: File?,
        disposer: ImageDisposer,
        result: Result,
        callBack: GetImageCallBack<Result>
    ) {
        disposer.dispose(originPath, targetFile, object : CompressListener {
            override fun onStart(path: String) {
                callBack.onDisposeStart()
            }

            override fun onFinish(compressed: Bitmap, savedFile: File?) {
                when (result) {
                    is CaptureResult,
                    is PickResult -> {
                        result.compressBitmap = compressed
                        result.targetFile = savedFile
                        DevUtil.d(Constant.TAG, "onSuccess $result")
                        callBack.onSuccess(result)
                        WorkThread.release()
                    }
                }
            }

            override fun onError(e: Exception) {
                callBack.onFailed(e)
            }
        })
    }
}