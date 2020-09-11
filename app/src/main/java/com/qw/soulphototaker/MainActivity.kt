package com.qw.soulphototaker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.SimpleGetImageAdapter
import com.qw.photo.dispose.QualityCompressor
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.pojo.BaseResult
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewComponent()
        CoCo.setDebug(true)
        CoCo.with(this).dispose().apply(object:CoCoCallBack<DisposeResult>{
            override fun onSuccess(data: DisposeResult) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFailed(exception: Exception) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun initViewComponent() {
        btn_capture.apply {
            setOnClickListener {
                CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .applyWithDispose()
                    .start(object : SimpleGetImageAdapter<TakeResult>() {

                        override fun onSuccess(data: TakeResult) {
                            Toast.makeText(this@MainActivity, "拍照操作最终成功", Toast.LENGTH_SHORT).show()
                            iv_image.setImageBitmap(data.compressBitmap)
                        }

                    })
            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, TakePictureActivity::class.java))
                true
            }
        }
        btn_pick.apply {
            setOnClickListener {
                CoCo.with(this@MainActivity)
                    .pick(createSDCardFile())
                    .apply()
//                .applyWithDispose()
                    .start(object : SimpleGetImageAdapter<PickResult>() {

                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                "选择操作最终成功 path: ${data.originUri}",
                                Toast.LENGTH_SHORT
                            ).show()
                            //if you want origin image
                            iv_image.setImageURI(data.originUri)
//                         if you use applyWithDispose() can get compress bitmap
//                        iv_image.setImageBitmap(data.compressBitmap)

                        }
                    })
            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, PickPictureActivity::class.java))
                true
            }
        }
        btn_custom_disposer.setOnClickListener {
            CoCo.with(this)
                .take(createSDCardFile())
                .applyWithDispose(CustomDisposer())
                .start(object : SimpleGetImageAdapter<TakeResult>() {

                    override fun onSuccess(data: TakeResult) {
                        Toast.makeText(this@MainActivity, "自定义Disposer拍照操作最终成功", Toast.LENGTH_SHORT)
                            .show()
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                })
        }
    }


    @Throws(IOException::class)
    private fun createSDCardFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(externalCacheDir!!.path + "/" + timeStamp)
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    /**
     * 自定义图片处理器
     * 自定义想要处理的任意结果
     */
    class CustomDisposer : ImageDisposer {

        override fun disposeImage(originPath: String, targetSaveFile: File?): BaseResult {
            return BaseResult().also {
                val bitmap = QualityCompressor()
                    .compress(originPath, 5)
                it.targetFile = targetSaveFile
                it.compressBitmap = bitmap
            }
        }

    }
}
