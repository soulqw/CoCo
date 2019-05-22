package com.qw.soulphototaker

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.ResultData
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        findViewById<View>(R.id.btn_capture).setOnClickListener {
//            View.OnClickListener {
                CoCo.with(this@MainActivity)
                    .take()
                    .uri(createUri())
                    .apply()
                    .start(object : BaseCallBack {
                        override fun onSuccess(data: ResultData) {
                            Toast.makeText(this@MainActivity, "拍照成功: ${data.path}", Toast.LENGTH_SHORT).show()
                            iv_image.setImageBitmap(data.thumbnailData)
                        }

                        override fun onFailed(exception: Exception) {
                            Toast.makeText(this@MainActivity, "拍照异常", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
//        }



    fun createUri(): Uri? {
        try {
            val file = createImageFile()
            file.also {
                return Uri.fromFile(file)
            }
        } catch (ex: java.lang.Exception) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
        }
        return null
    }

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
