package com.qw.soulphototaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.Utils
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.dispose.ImageDisposer
import com.qw.photo.pojo.PickResult
import kotlinx.android.synthetic.main.activity_funtion_detail.*


/**
 * @author cd5160866
 */
class PickPictureActivity : BaseFunctionActivity() {

    override fun start(isMatrix: Boolean, degree: Int) {
        if (degree == -1) {
            CoCo.with(this)
                .pick(createSDCardFile())
                .apply()
                .start(object : GetImageCallBack<PickResult> {
                    override fun onSuccess(data: PickResult) {
                        val selectedPath = Utils.uriToImagePath(this@PickPictureActivity, data.originUri)
                        val bitmap: Bitmap = BitmapFactory.decodeFile(selectedPath)
                        getImageView().setImageBitmap(bitmap)
                        tv_result.text = getImageSizeDesc(bitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@PickPictureActivity, "选择异常: $exception", Toast.LENGTH_SHORT).show()
                    }

                })
        } else {
            val strategy: CompressStrategy = if (isMatrix) {
                CompressStrategy.MATRIX
            } else {
                CompressStrategy.QUALITY
            }
            CoCo.with(this)
                .pick(createSDCardFile())
                .applyWithDispose(
                    ImageDisposer().degree(degree)
                        .strategy(strategy)
                ).start(object : GetImageCallBack<PickResult> {

                    override fun onDisposeStart() {
                        Toast.makeText(this@PickPictureActivity, "选择成功,开始处理", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(data: PickResult) {
                        Toast.makeText(
                            this@PickPictureActivity,
                            "选择操作最终成功 path: ${data.originUri.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        getImageView().setImageBitmap(data.compressBitmap)
                        tv_result.text = getImageSizeDesc(data.compressBitmap!!)
                    }

                    override fun onCancel() {
                        Toast.makeText(this@PickPictureActivity, "选择取消", Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@PickPictureActivity, "选择异常: $exception", Toast.LENGTH_SHORT).show()

                    }
                })
        }
    }
}
