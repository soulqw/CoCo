package com.qw.soulphototaker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.Disposer
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.PickResult
import kotlinx.android.synthetic.main.activity_funtion_detail.*
import kotlinx.android.synthetic.main.activity_take_photo.*
import kotlinx.android.synthetic.main.activity_take_photo.iv_image


/**
 * @author cd5160866
 */
class DebugDisposerActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funtion_detail)
        setSupportActionBar(toolbar)
        title = "About DefaultImageDisposer"
        sb_degree.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                (findViewById<View>(R.id.tv_degree) as TextView).text = progress.toString() + ""
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        cb_compress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ly_compress.visibility = View.VISIBLE
            } else {
                ly_compress.visibility = View.GONE
            }
        }
        btn_begin.setOnClickListener {
            val isMatrix = rg_strategy.checkedRadioButtonId == R.id.matrix
            val degree = if (cb_compress.isChecked) {
                sb_degree.progress
            } else {
                -1
            }
            start(isMatrix, degree)
        }
    }

    fun start(isMatrix: Boolean, degree: Int) {
        if (degree == -1) {
            CoCo.with(this)
                .pick()
                .start(object : CoCoCallBack<PickResult> {
                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }

                    override fun onSuccess(data: PickResult) {
                        iv_image.setImageURI(data.originUri)
                    }
                })
        } else {

            val strategy: CompressStrategy = if (isMatrix) {
                CompressStrategy.MATRIX
            } else {
                CompressStrategy.QUALITY
            }

            //set the params for DefaultImageDisposer
            val disposer: Disposer = DefaultImageDisposer()
                .degree(degree)
                .strategy(strategy)

            CoCo.with(this)
                //you can also use pick for this DefaultImageDisposer
//                .take(createSDCardFile())
                .pick()
                .then()
                .dispose(disposer)
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }
                })
        }
    }
}
