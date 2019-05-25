package com.qw.soulphototaker

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.qw.photo.CoCo
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.ResultData
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, object :
            CheckRequestPermissionListener {

            override fun onPermissionOk(permission: Permission?) {
                Toast.makeText(this@MainActivity, "权限成功", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(permission: Permission?) {
                Toast.makeText(this@MainActivity, "权限失败", Toast.LENGTH_SHORT).show()
            }
        })
        btn_capture.setOnClickListener {
            CoCo.with(this@MainActivity)
                .take(createImageFile())
                .apply()
                .start(object : BaseCallBack {

                    override fun onSuccess(data: ResultData) {
                        Toast.makeText(this@MainActivity, "拍照成功 path: ${data.file?.path}", Toast.LENGTH_SHORT).show()
//                        iv_image.setImageBitmap(data.thumbnailData)
                        Glide.with(this@MainActivity).load(data.file).into(iv_image)
                    }

                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@MainActivity, "拍照异常: " + exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
        }

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
            // Save a file: xml for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
