package com.qw.soulphototaker

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.PickCallBack
import com.qw.photo.constant.Range
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_funtion_detail.iv_image
import kotlinx.android.synthetic.main.activity_take_photo.*
import kotlin.system.exitProcess


/**
 * @author cd5160866
 */
class PickPictureActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_photo)
        setSupportActionBar(toolbar)
        title = "PickPhotoDetail"

        //base usage
        base.setOnClickListener {
            CoCo.with(this@PickPictureActivity)
                .pick()
                .start(object : CoCoCallBack<PickResult> {

                    override fun onSuccess(data: PickResult) {
                        iv_image.setImageURI(data.originUri)
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //other functions in take operate
        others.setOnClickListener {
            CoCo.with(this@PickPictureActivity)
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
                            this@PickPictureActivity,
                            "need permission first to pick Photo",
                            Toast.LENGTH_SHORT
                        ).show()
                        Handler().postDelayed({ exitProcess(0) }, 500);
                    }

                })
        }
    }

    fun pickAndDisposeImage() {
        CoCo.with(this@PickPictureActivity)
            .pick()
            .then()
            .dispose()
            .fileToSaveResult(createSDCardFile())
            .start(object : CoCoCallBack<DisposeResult> {

                override fun onSuccess(data: DisposeResult) {
                    iv_image.setImageBitmap(data.compressBitmap)
                }

                override fun onFailed(exception: Exception) {
                }
            })
    }
}
