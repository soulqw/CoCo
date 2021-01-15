package com.qw.soulphototaker

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.qw.curtain.lib.Curtain
import com.qw.curtain.lib.shape.CircleShape
import com.qw.curtain.lib.shape.RoundShape
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.CompressStrategy
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.pojo.DisposeResult
import kotlinx.android.synthetic.main.activity_funtion_detail.*
import java.io.ByteArrayOutputStream


/**
 * @author cd5160866
 */
class DebugDefaultDisposerActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funtion_detail)

        cb_compress.setOnCheckedChangeListener { _, isChecked ->
            findViewById<View>(R.id.ly_compress).visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        sb_degree.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                (findViewById<View>(R.id.tv_degree) as TextView).text = progress.toString() + ""
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        btn_begin.setOnClickListener {
            val compressStrategy = if (rg_strategy.checkedRadioButtonId == R.id.matrix) {
                CompressStrategy.MATRIX
            } else {
                CompressStrategy.QUALITY
            }
            val degree = if (cb_compress.isChecked) {
                sb_degree.progress
            } else {
                -1
            }
            start(compressStrategy, degree)
        }

        Curtain(this)
            .withShape(sb_degree,RoundShape(6f))
            .withPadding(sb_degree,20)
            .show()
    }

    private fun start(compressStrategy: CompressStrategy, degree: Int) {
        //build default disposer
        val disposer = DefaultImageDisposer()
            .strategy(compressStrategy)
            .degree(degree)

        CoCo.with(this)
            .take(createSDCardFile())
            .then()
            .dispose(disposer)
            .start(object : CoCoCallBack<DisposeResult> {
                override fun onSuccess(data: DisposeResult) {
                    iv_image.setImageBitmap(data.compressBitmap)
                    tv_result.text = getImageSizeDesc(data.compressBitmap)
                }

                override fun onFailed(exception: Exception) {
                    Log.d("CoCo", exception.toString())
                }
            })
    }

    private fun getImageSizeDesc(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return "图片：with: ${bitmap.width} height: ${bitmap.height}size: ${data.size}"
    }

}
