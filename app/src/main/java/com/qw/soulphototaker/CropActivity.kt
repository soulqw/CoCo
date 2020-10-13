package com.qw.soulphototaker

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.PickCallBack
import com.qw.photo.constant.Range
import com.qw.photo.pojo.CropResult
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_funtion_detail.iv_image
import kotlinx.android.synthetic.main.activity_take_photo.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.system.exitProcess


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
                .crop(createSDCardFile(), 30, 30, imageFile)
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
                .pick()
                .range(Range.PICK_CONTENT)
//                .range(Range.PICK_DICM)
                .callBack(object : PickCallBack {

                    override fun onFinish(result: PickResult) {
                        Log.d(MainActivity.TAG, "take onFinish${result}")
                    }

                    override fun onCancel() {
                        Log.d(MainActivity.TAG, "take onCancel")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "take onStart")
                    }

                }).start(object : CoCoCallBack<PickResult> {

                    override fun onSuccess(data: PickResult) {
                        iv_image.setImageURI(data.originUri)
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //work with other operate
        //pick photo first then dispose image to makes result smaller
        comb.setOnClickListener {
            //dispose need operate IO APi, so if you dispose sd card file you will need storage permission
            //https://github.com/soulqw/SoulPermission
            SoulPermission.getInstance()
                .checkAndRequestPermission("android.permission.READ_EXTERNAL_STORAGE", object :
                    CheckRequestPermissionListener {

                    override fun onPermissionOk(permission: Permission?) {
                        pickAndDisposeImage()
                    }

                    override fun onPermissionDenied(permission: Permission?) {
                        Toast.makeText(
                            this@CropActivity,
                            "need permission first to pick Photo",
                            Toast.LENGTH_SHORT
                        ).show()
                        Handler().postDelayed({ exitProcess(0) }, 500);
                    }

                })
        }
    }

    fun pickAndDisposeImage() {
        CoCo.with(this@CropActivity)
            .pick()
            .then()
            .dispose()
            .then()
            .crop(createSDCardFile(), 1080, 1920)
//            .fileToSaveResult(createSDCardFile())
            .start(object : CoCoCallBack<CropResult> {

                override fun onSuccess(data: CropResult) {
                    iv_image.setImageBitmap(data.cropBitmap)
                }

                override fun onFailed(exception: Exception) {
                }
            })
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
            val inStream: InputStream = resources.assets.open("ic_launcher_round.png")
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
