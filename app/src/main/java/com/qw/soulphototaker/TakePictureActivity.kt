package com.qw.soulphototaker

import android.os.Bundle
import android.util.Log
import com.qw.photo.CoCo
import com.qw.photo.Utils
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.TakeCallBack
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_take_photo.*

/**
 * @author cd5160866
 */
class TakePictureActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
        setSupportActionBar(toolbar)
        title = "TakePhotoDetail"

        //base usage
        base.setOnClickListener {
            CoCo.with(this@TakePictureActivity)
                .take(createSDCardFile())
                .start(object : CoCoCallBack<TakeResult> {

                    override fun onSuccess(data: TakeResult) {
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile.absolutePath))
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //other functions in take operate
        others.setOnClickListener {
            CoCo.with(this@TakePictureActivity)
                .take(createSDCardFile())
                .cameraFace(TakeBuilder.FRONT)
                .callBack(object : TakeCallBack {

                    override fun onFinish(result: TakeResult) {
                        Log.d(MainActivity.TAG, "take onFinish${result}")
                    }

                    override fun onCancel() {
                        Log.d(MainActivity.TAG, "take onCancel")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "take onStart")
                    }

                }).start(object : CoCoCallBack<TakeResult> {

                    override fun onSuccess(data: TakeResult) {
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile.absolutePath))
                    }

                    override fun onFailed(exception: Exception) {}
                })
        }

        //work with other operate
        //take photo first then dispose image to makes result smaller
        comb.setOnClickListener {
            CoCo.with(this@TakePictureActivity)
                .take(createSDCardFile())
                .then()
                .dispose()
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                    }

                })
        }


    }
}
