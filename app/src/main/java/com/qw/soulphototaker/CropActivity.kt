package com.qw.soulphototaker

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.CropCallBack
import com.qw.photo.pojo.CropResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_crop_photo.*
import kotlinx.android.synthetic.main.activity_funtion_detail.iv_image
import kotlinx.android.synthetic.main.activity_take_photo.base
import kotlinx.android.synthetic.main.activity_take_photo.others
import kotlinx.android.synthetic.main.activity_take_photo.toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


/**
 * @author cd5160866
 */
class CropActivity : BaseToolbarActivity() {

    private lateinit var imageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_photo)
        setSupportActionBar(toolbar)
        title = "CropPhotoDetail"
        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                object : CheckRequestPermissionListener {

                    override fun onPermissionOk(permission: Permission?) {
                        createDefaultFile()
                    }

                    override fun onPermissionDenied(permission: Permission?) {
                        Toast.makeText(
                            applicationContext,
                            "Need permission to start",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                })

        //base usage
        base.setOnClickListener {
            CoCo.with(this@CropActivity)
                .crop(imageFile)
                .start(object : CoCoCallBack<CropResult> {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //other functions in take operate
        others.setOnClickListener {
            CoCo.with(this@CropActivity)
                .crop(imageFile)
                .callBack(object : CropCallBack {

                    override fun onFinish(result: CropResult) {
                        Log.d(MainActivity.TAG, "crop onFinish $result")
                    }

                    override fun onCancel() {
                        Log.d(MainActivity.TAG, "crop onCancel")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "crop onStart")
                    }

                }).start(object : CoCoCallBack<CropResult> {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //work with other operate 1
        //pick photo first then crop image
        comb_1.setOnClickListener {
            CoCo.with(this@CropActivity)
                .pick()
                .then()
                .crop()
                .start(object : CoCoCallBack<CropResult> {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                    }
                })
        }

        //work with other operate 2
        //take then dispose then crop
        comb_2.setOnClickListener {
            CoCo.with(this@CropActivity)
                .take(createSDCardFile())
                .then()
                .dispose()
                .then()
                .crop()
                .start(object : CoCoCallBack<CropResult> {

                    override fun onSuccess(data: CropResult) {
                        iv_image.setImageBitmap(data.cropBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                    }
                })
        }
    }

    private fun createDefaultFile() {
        imageFile = createSDCardFile()
        createSdImageFile(imageFile.path)
    }

    /**
     * create a file from asset Path
     */
    private fun createSdImageFile(fileName: String) {
        return try {
            val inStream: InputStream = resources.assets.open("coco_demo.webp")
            val outStream = FileOutputStream(fileName, true)
            val buffer = ByteArray(1024)
            var len = -1
            while (inStream.read(buffer).also { len = it } != -1) {
                outStream.write(buffer, 0, len)
            }
            outStream.close()
            inStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}
