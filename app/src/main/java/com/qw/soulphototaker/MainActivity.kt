package com.qw.soulphototaker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.BaseCallBack
import com.qw.photo.pojo.ResultData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoCo.with(this)
            .take()
            .apply()
            .start(object : BaseCallBack {
                override fun onSuccess(data: ResultData) {
                    Toast.makeText(this@MainActivity, "拍照成功", Toast.LENGTH_SHORT).show()
                    iv_image.setImageBitmap(data.thumbnailData)
                }

                override fun onFailed(exception: Exception) {

                }
            })
    }
}
