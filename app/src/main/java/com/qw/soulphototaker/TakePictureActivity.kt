package com.qw.soulphototaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_funtion_detail.*


/**
 * @author cd5160866
 */
class TakePictureActivity : BaseFunctionActivity() {

    override fun start(isFragment: Boolean, isMatrix: Boolean, degree: Int) {
        if (isFragment) {
            val fragment: FunctionFragment =
                supportFragmentManager.findFragmentByTag(TAG) as FunctionFragment
            fragment.takePhoto(isMatrix, degree)
            return
        }
        if (degree == -1) {
//            CoCo.with(this)
//                .take(createSDCardFile())
//                .apply()
//                .start(object : GetImageCallBack<TakeResult> {
//                    override fun onSuccess(data: TakeResult) {
//                        Toast.makeText(this@TakePictureActivity, "拍照操作最终成功", Toast.LENGTH_SHORT)
//                            .show()
//                        val bitmap: Bitmap = BitmapFactory.decodeFile(data.targetFile!!.path)
//                        getImageView().setImageBitmap(bitmap)
//                        tv_result.text = getImageSizeDesc(bitmap)
//                    }
//
//                    override fun onFailed(exception: Exception) {
//                        Toast.makeText(
//                            this@TakePictureActivity,
//                            "拍照异常: $exception",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                })
        } else {
            val strategy: CompressStrategy = if (isMatrix) {
                CompressStrategy.MATRIX
            } else {
                CompressStrategy.QUALITY
            }
            var cameraFace = TakeBuilder.BACK
            if (tb_camera_face.isChecked) {
                cameraFace = TakeBuilder.FRONT
            }
//            CoCo.with(this@TakePictureActivity)
//                .take(createSDCardFile())
//                .cameraFace(cameraFace)
//                .applyWithDispose(
//                    DefaultImageDisposer().degree(degree)
//                        .strategy(strategy)
//                )
//                .start(object : GetImageCallBack<TakeResult> {
//
//                    override fun onDisposeStart() {
//                        Toast.makeText(this@TakePictureActivity, "拍照成功,开始处理", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                    override fun onCancel() {
//                        Toast.makeText(this@TakePictureActivity, "拍照取消", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onFailed(exception: Exception) {
//                        Toast.makeText(
//                            this@TakePictureActivity,
//                            "拍照异常: $exception",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    override fun onSuccess(data: TakeResult) {
//                        Toast.makeText(this@TakePictureActivity, "拍照操作最终成功", Toast.LENGTH_SHORT)
//                            .show()
//                        getImageView().setImageBitmap(data.compressBitmap)
//                        tv_result.text = getImageSizeDesc(data.compressBitmap!!)
//                    }
//
//                })
        }
    }
}
