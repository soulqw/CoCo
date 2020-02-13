package com.qw.soulphototaker

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_funtion_detail.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author cd5160866
 */
abstract class BaseFunctionActivity : AppCompatActivity() {

    companion object {
        const val TAG = "functionFragment"
    }

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
        tb_container.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                iv_image.visibility = View.GONE
                tv_result.visibility = View.GONE
                fl_fragment.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_fragment, FunctionFragment(), TAG)
                    .commitAllowingStateLoss()
            } else {
                tv_result.visibility = View.VISIBLE
                iv_image.visibility = View.VISIBLE
                fl_fragment.visibility = View.GONE
            }
        }
        btn_begin.setOnClickListener {
            val isMatrix = rg_strategy.checkedRadioButtonId == R.id.matrix
            val degree = if (cb_compress.isChecked) {
                sb_degree.progress
            } else {
                -1
            }
            start(tb_container.isChecked, isMatrix, degree)
        }
    }

    protected abstract fun start(isFragment: Boolean, isMatrix: Boolean, degree: Int)

    protected fun getImageView(): ImageView {
        return iv_image
    }

    protected fun getImageSizeDesc(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return "图片：with: ${bitmap.width} height: ${bitmap.height}size: ${data.size}"
    }

    @Throws(IOException::class)
    protected fun createSDCardFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(externalCacheDir!!.path + "/" + timeStamp)
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}
