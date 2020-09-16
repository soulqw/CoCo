package com.qw.soulphototaker

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.DisposeCallBack
import com.qw.photo.callback.PickCallBack
import com.qw.photo.callback.TakeCallBack
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseToolbarActivity() {

    companion object {
        const val TAG = "CoCoDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SoulPermission.getInstance()
            .checkAndRequestPermission("android.permission.READ_EXTERNAL_STORAGE", object :
                CheckRequestPermissionListener {
                override fun onPermissionOk(permission: Permission?) {
                    initViewComponent()
                }

                override fun onPermissionDenied(permission: Permission?) {
                    Toast.makeText(
                        this@MainActivity,
                        "need permission first to pick Photo",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

            })
        CoCo.setDebug(BuildConfig.DEBUG)
    }

    private fun initViewComponent() {
        btn_capture.setOnClickListener {
            CoCo.with(this@MainActivity)
                .take(createSDCardFile())
                .start(object : CoCoCallBack<TakeResult> {

                    override fun onSuccess(data: TakeResult) {
                        Toast.makeText(
                            this@MainActivity,
                            data.savedFile.absolutePath.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailed(exception: Exception) {
                    }
                })
        }
        btn_capture.setOnLongClickListener {
            startActivity(Intent(this@MainActivity, TakePictureActivity::class.java))
            true
        }
        btn_pick.apply {
            setOnClickListener {
                CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoCallBack<PickResult> {
                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                data.originUri.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailed(exception: Exception) {
                        }
                    })
            }
            setOnLongClickListener {
//                startActivity(Intent(this@MainActivity, TakePictureActivity::class.java))
                CoCo.with(this@MainActivity)
                    .pick()
                    .callBack(object : PickCallBack {

                        override fun onFinish(result: PickResult) {
                            Log.d(TAG, "pick onFinish${result}")
                        }

                        override fun onCancel() {
                            Log.d(TAG, "pick onCancel")
                        }

                        override fun onStart() {
                            Log.d(TAG, "pick onStart")
                        }
                    })
                    .then()
                    .dispose()
                    .target(createSDCardFile())
                    .callBack(object : DisposeCallBack {

                        override fun onFinish(result: DisposeResult) {
                            Log.d(TAG, "dispose onFinish")
                        }

                        override fun onStart() {
                            Log.d(TAG, "dispose onStart")
                        }

                    })
                    .start(object : CoCoCallBack<DisposeResult> {
                        override fun onSuccess(data: DisposeResult) {
                            iv_image.setImageBitmap(data.compressBitmap)
                        }

                        override fun onFailed(exception: Exception) {
                            Log.d(TAG, "final onFailed${exception}")
                        }
                    })
                true
            }
        }
//        btn_pick.apply {
//            setOnClickListener {
//                CoCo.with(this@MainActivity)
//                    .pick(createSDCardFile())
//                    .apply()
////                .applyWithDispose()
//                    .start(object : SimpleGetImageAdapter<PickResult>() {
//
//                        override fun onSuccess(data: PickResult) {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "选择操作最终成功 path: ${data.originUri}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            //if you want origin image
//                            iv_image.setImageURI(data.originUri)
////                         if you use applyWithDispose() can get compress bitmap
////                        iv_image.setImageBitmap(data.compressBitmap)
//
//                        }
//                    })
//            }
//            setOnLongClickListener {
//                startActivity(Intent(this@MainActivity, PickPictureActivity::class.java))
//                true
//            }
//        }
//        btn_custom_disposer.setOnClickListener {
//            CoCo.with(this)
//                .take(createSDCardFile())
//                .applyWithDispose(CustomDisposer())
//                .start(object : SimpleGetImageAdapter<TakeResult>() {
//
//                    override fun onSuccess(data: TakeResult) {
//                        Toast.makeText(this@MainActivity, "自定义Disposer拍照操作最终成功", Toast.LENGTH_SHORT)
//                            .show()
//                        iv_image.setImageBitmap(data.compressBitmap)
//                    }
//
//                })
//        }
    }


    /**
     * 自定义图片处理器
     * 自定义想要处理的任意结果
     */
//    class CustomDisposer : ImageDisposer {
//
//        override fun disposeImage(originPath: String, targetSaveFile: File?): BaseResult {
//            return BaseResult().also {
//                val bitmap = QualityCompressor()
//                    .compress(originPath, 5)
//                it.targetFile = targetSaveFile
//                it.compressBitmap = bitmap
//            }
//        }
//
//    }
}
