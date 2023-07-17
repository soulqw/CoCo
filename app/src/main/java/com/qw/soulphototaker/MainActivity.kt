package com.qw.soulphototaker


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.CoCoConfigs
import com.qw.photo.Utils
import com.qw.photo.callback.CoCoAdapter
import com.qw.photo.constant.Range
import com.qw.photo.constant.Type

import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.iv_image

class MainActivity : BaseToolbarActivity() {

    companion object {
        const val TAG = "CoCoDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewComponent()
        CoCoConfigs.apply {
            setDebug(BuildConfig.DEBUG)
            configCropsResultFile(createSDCardFile().absolutePath)
        }
    }

    private fun initViewComponent() {
        btn_capture.setOnClickListener {
            CoCo.with(this@MainActivity)
                .take(createSDCardFile())
                .start(object : CoCoAdapter<TakeResult>() {

                    override fun onSuccess(data: TakeResult) {
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
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
                    .range(Range.PICK_CONTENT)
                    .type(Type.JPEG)
                    .start(object : CoCoAdapter<PickResult>() {
                        override fun onSuccess(data: PickResult) {
                            Toast.makeText(
                                this@MainActivity,
                                data.originUri.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            iv_image.setImageURI(data.originUri)
                        }
                    })

            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, PickPictureActivity::class.java))
                true
            }
        }
        btn_dispose.apply {
            setOnClickListener {

                CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .then()
                    .dispose()
                    .start(object : CoCoAdapter<DisposeResult>() {

                        override fun onSuccess(data: DisposeResult) {
                            iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                        }
                    })

            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, DisposeActivity::class.java))
                true
            }
        }

        btn_crop.apply {
            setOnClickListener {
                //request permission
                SoulPermission.getInstance()
                    .checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        object : CheckRequestPermissionListener {
                            override fun onPermissionOk(permission: Permission?) {

                                CoCo.with(this@MainActivity)
                                    .pick()
                                    .then()
                                    .crop()
                                    .then()
                                    .dispose()
                                    .start(object : CoCoAdapter<DisposeResult>() {

                                        override fun onSuccess(data: DisposeResult) {
                                            iv_image.setImageBitmap(data.compressBitmap)
                                        }
                                    })
                            }

                            override fun onPermissionDenied(permission: Permission?) {
                                Toast.makeText(
                                    applicationContext,
                                    "need file permission first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

            }
            setOnLongClickListener {
                startActivity(Intent(this@MainActivity, CropActivity::class.java))
                true
            }
        }
    }

}
