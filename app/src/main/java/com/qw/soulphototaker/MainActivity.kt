package com.qw.soulphototaker


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.Utils
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.pojo.CropResult

import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.iv_image
import kotlinx.android.synthetic.main.activity_take_photo.*

class MainActivity : BaseToolbarActivity() {

    companion object {
        const val TAG = "CoCoDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewComponent()
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
                            data.savedFile!!.absolutePath.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
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
        btn_dispose.apply {
            setOnClickListener {

                CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .then()
                    .dispose()
                    .start(object : CoCoCallBack<DisposeResult> {

                        override fun onSuccess(data: DisposeResult) {
                            iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                        }

                        override fun onFailed(exception: Exception) {

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

                CoCo.with(this@MainActivity)
                    .take(createSDCardFile())
                    .then()
                    .crop(createSDCardFile())
                    .start(object : CoCoCallBack<CropResult> {

                        override fun onSuccess(data: CropResult) {
                            iv_image.setImageBitmap(data.cropBitmap)
                        }

                        override fun onFailed(exception: Exception) {
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
