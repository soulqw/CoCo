package com.qw.soulphototaker

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.qw.curtain.lib.Curtain
import com.qw.curtain.lib.CurtainFlow
import com.qw.curtain.lib.flow.CurtainFlowInterface
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
            val degree = sb_degree.progress
            start(compressStrategy, degree)
        }

        showGuide()
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

    /**
     * https://github.com/soulqw/Curtain
     */
    private fun showGuide() {

        val STEP1 = 1

        val STEP2 = 2

        CurtainFlow
            .Builder()
            .with(STEP1, Curtain(this)
                    .with(rg_strategy)
                    .setTopView(R.layout.view_guide_1))
            .with(STEP2, Curtain(this)
                    .with(sb_degree)
                    .setTopView(R.layout.view_guide_2))
            .create()
            .start(object : CurtainFlow.CallBack {

                override fun onProcess(currentId: Int, curtainFlow: CurtainFlowInterface?) {
                    curtainFlow!!.findViewInCurrentCurtain<View>(R.id.tv_i_know)
                        .setOnClickListener {
                            if (currentId == STEP1) {
                                curtainFlow.push()
                            } else {
                                curtainFlow.finish()
                            }
                        }
                }

                override fun onFinish() {
                }
            })
    }

}
