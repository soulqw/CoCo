package com.qw.soulphototaker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
                .take(createSDCardFile())
//                .apply()
                .applyWithDispose()
                .start(object : BaseCallBack {

                    override fun onDisposeStart() {
                        Toast.makeText(this@MainActivity, "拍照成功,开始处理", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(data: ResultData) {
//                        Toast.makeText(this@MainActivity, "拍照成功", Toast.LENGTH_SHORT)
//                            .show()
                        Glide.with(this@MainActivity).load(data.targetFile).into(iv_image)
//                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "拍照取消", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@MainActivity, "拍照异常: $exception", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        btn_pick.setOnClickListener {
            CoCo.with(this)
                .pick()
                .targetFile(createSDCardFile())
                .applyWithDispose()
//                .applyWithDispose(ImageDisposer()
//                    .degree(10)
//                    .strategy(CompressStrategy.QUALITY))
                .start(object : BaseCallBack {

                    override fun onSuccess(data: ResultData) {
                        Toast.makeText(this@MainActivity, "选择成功 path: ${data.uri?.path}", Toast.LENGTH_SHORT).show()
                        Log.d("qw", "with ${data.compressBitmap?.width} height ${data.compressBitmap?.height}")
                        iv_image.setImageBitmap(data.compressBitmap)
//                        val path = Utils.uriToImagePath(this@MainActivity, data.uri!!)
//                        val file = File(path)
//                        if (!file.exists()) {
//                            file.mkdir()
//                        }
//                        Glide.with(this@MainActivity).load(file).into(iv_image)
//                        var bitmap: Bitmap? = BitmapFactory.decodeFile(path)
//                        if (null != bitmap) {
//                            bitmap = Utils.zoomBitmap(bitmap, 0.5f)
//                            iv_image.setImageBitmap(bitmap)
//                        }
                    }

                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "选择取消", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(exception: Exception) {
                        Toast.makeText(this@MainActivity, "选择异常: " + exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
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

    private fun galleryAddPic(path: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(path)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
}
