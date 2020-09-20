package com.qw.soulphototaker

import android.os.Bundle
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.pojo.DisposeResult
import kotlinx.android.synthetic.main.activity_dispose.*
import java.io.File

/**
 * @author cd5160866
 */
class DisposeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispose)
        setSupportActionBar(toolbar)
        title = "DisposeDetail"

        base.setOnClickListener {

            CoCo.with(this)
                .dispose()
                .origin("file:///android_asset/ic_launcher_round.png")
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
