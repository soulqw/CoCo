package com.qw.soulphototaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.qw.photo.CoCo
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.functions.PickBuilder
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_funtion_detail.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author cd5160866
 * @date 2019-09-28
 */
class FunctionFragment : Fragment() {

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_coco, null)
        return rootView
    }

    fun takePhoto(isMatrix: Boolean, degree: Int) {
        if (degree == -1) {
            CoCo.with(this)
                .take(createSDCardFile())
                .apply()
                .start(object : GetImageCallBack<TakeResult> {
                    override fun onSuccess(data: TakeResult) {
                        Toast.makeText(context, "拍照操作最终成功", Toast.LENGTH_SHORT)
                            .show()
                        val bitmap: Bitmap = BitmapFactory.decodeFile(data.targetFile!!.path)
                        iv_image.setImageBitmap(bitmap)
                        tv_result.text = getImageSizeDesc(bitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(
                            context,
                            "拍照异常: $exception",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        } else {
            val strategy: CompressStrategy = if (isMatrix) {
                CompressStrategy.MATRIX
            } else {
                CompressStrategy.QUALITY
            }
            var cameraFace = TakeBuilder.BACK
            if (activity!!.findViewById<ToggleButton>(R.id.tb_camera_face).isChecked) {
                cameraFace = TakeBuilder.FRONT
            }
            CoCo.with(this)
                .take(createSDCardFile())
                .cameraFace(cameraFace)
                .applyWithDispose(
                    DefaultImageDisposer().degree(degree)
                        .strategy(strategy)
                )
                .start(object : GetImageCallBack<TakeResult> {

                    override fun onDisposeStart() {
                        Toast.makeText(context, "拍照成功,开始处理", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onCancel() {
                        Toast.makeText(context, "拍照取消", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(
                            context,
                            "拍照异常: $exception",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSuccess(data: TakeResult) {
                        Toast.makeText(context, "拍照操作最终成功", Toast.LENGTH_SHORT)
                            .show()
                        iv_image.setImageBitmap(data.compressBitmap)
                        tv_result.text = getImageSizeDesc(data.compressBitmap!!)
                    }

                })
        }
    }

    fun pick(isMatrix: Boolean, degree: Int) {
        if (degree == -1) {
            CoCo.with(this)
                .pick(createSDCardFile())
                .range(PickBuilder.PICK_CONTENT)
                .apply()
                .start(object : GetImageCallBack<PickResult> {
                    override fun onSuccess(data: PickResult) {
                        val bitmap: Bitmap = BitmapFactory.decodeFile(data.localPath)
                        iv_image.setImageBitmap(bitmap)
                        tv_result.text = getImageSizeDesc(bitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(context, "选择异常: $exception", Toast.LENGTH_SHORT).show()
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
                .range(PickBuilder.PICK_CONTENT)
                .applyWithDispose(
                    DefaultImageDisposer().degree(degree)
                        .strategy(strategy)
                ).start(object : GetImageCallBack<PickResult> {

                    override fun onDisposeStart() {
                        Toast.makeText(context, "选择成功,开始处理", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(data: PickResult) {
                        Toast.makeText(
                            context,
                            "选择操作最终成功 path: ${data.originUri.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        iv_image.setImageBitmap(data.compressBitmap)
                        tv_result.text = getImageSizeDesc(data.compressBitmap!!)
                    }

                    override fun onCancel() {
                        Toast.makeText(context, "选择取消", Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(context, "选择异常: $exception", Toast.LENGTH_SHORT).show()

                    }
                })
        }
    }


    private fun getImageSizeDesc(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return "图片：with: ${bitmap.width} height: ${bitmap.height}size: ${data.size}"
    }

    @Throws(IOException::class)
    private fun createSDCardFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(requireActivity().externalCacheDir!!.path + "/" + timeStamp)
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }


}
