package com.qw.soulphototaker


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_main.*

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
                            iv_image.setImageURI(data.originUri)
                        }

                        override fun onFailed(exception: Exception) {
                        }
                    })
            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, PickPictureActivity::class.java))
                true
            }
        }
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
