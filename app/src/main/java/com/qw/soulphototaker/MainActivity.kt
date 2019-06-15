package com.qw.soulphototaker

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.Utils
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.pojo.CaptureResult
import com.qw.photo.pojo.PickResult
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
        checkPermissionFirst()
        CoCo.setDebug(true)
    }

    //https://github.com/soulqw/SoulPermission
    private fun checkPermissionFirst() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, object :
            CheckRequestPermissionListener {

            override fun onPermissionOk(permission: Permission?) {
                initViewComponent()
            }

            override fun onPermissionDenied(permission: Permission?) {
                Toast.makeText(this@MainActivity, "不授予文件权限无法使用CoCo", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun initViewComponent() {
        btn_capture.apply {
            setOnClickListener {
                CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .applyWithDispose()
                    .start(object : GetImageCallBack<CaptureResult> {

                        override fun onDisposeStart() {
                            Toast.makeText(this@MainActivity, "拍照成功,开始处理", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@MainActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailed(exception: Exception) {
                            Toast.makeText(this@MainActivity, "拍照异常: $exception", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(data: CaptureResult) {
                            Toast.makeText(this@MainActivity, "拍照操作最终成功", Toast.LENGTH_SHORT).show()
                            iv_image.setImageBitmap(data.compressBitmap)
                        }

                    })
            }
            setOnLongClickListener(
                object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        startActivity(Intent(this@MainActivity, TakePictureActivity::class.java))
                        return true
                    }
                })
        }
        btn_pick.apply {
            setOnClickListener {
                CoCo.with(this@MainActivity)
                    .pick(createSDCardFile())
                    .apply()
//                .applyWithDispose()
                    .start(object : GetImageCallBack<PickResult> {

                        override fun onDisposeStart() {
                            Toast.makeText(this@MainActivity, "选择成功,开始处理", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                "选择操作最终成功 path: ${data.originUri.path}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val selectedPath = Utils.uriToImagePath(this@MainActivity, data.originUri)
                            val bitmap: Bitmap = BitmapFactory.decodeFile(selectedPath)
                            iv_image.setImageBitmap(bitmap)
//                         if you use applyWithDispose() can get compress bitmap
//                        iv_image.setImageBitmap(data.compressBitmap)

                        }

                        override fun onFailed(exception: Exception) {
                            Toast.makeText(this@MainActivity, "选择异常: $exception", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@MainActivity, "选择取消", Toast.LENGTH_SHORT).show()
                        }

                    })
            }
            setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    startActivity(Intent(this@MainActivity, PickPictureActivity::class.java))
                    return true
                }
            })
        }
    }


    @Throws(IOException::class)
    private fun createSDCardFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStorageDirectory()
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}
