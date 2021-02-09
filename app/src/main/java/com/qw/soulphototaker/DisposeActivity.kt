package com.qw.soulphototaker

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.DisposeCallBack
import com.qw.photo.dispose.QualityCompressor
import com.qw.photo.dispose.disposer.Disposer
import com.qw.photo.pojo.DisposeResult
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import kotlinx.android.synthetic.main.activity_dispose.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author cd5160866
 */
class DisposeActivity : BaseToolbarActivity() {
    private lateinit var imageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispose)
        setSupportActionBar(toolbar)
        title = "DisposeDetail"

        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                object : CheckRequestPermissionListener {

                    override fun onPermissionOk(permission: Permission?) {
                        createDefaultFile()
                        initComponent()
                    }

                    override fun onPermissionDenied(permission: Permission?) {
                        Toast.makeText(
                            applicationContext,
                            "Need permission to start",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                })
    }

    private fun createDefaultFile() {
        imageFile = createSDCardFile()
        createSdImageFile(imageFile.path)
    }

    private fun initComponent() {
        base.setOnClickListener {

            CoCo.with(this)
                .dispose()
                .origin(imageFile.path)
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })

        }

        others.setOnClickListener {
            CoCo.with(this)
                .dispose()
                .origin(imageFile.path)
                //custom to dispose image
                .disposer(CustomDisposer())
                //if set the origin file and the result file will both keep
                .fileToSaveResult(createSDCardFile("resultFile"))
                .callBack(object : DisposeCallBack {
                    override fun onFinish(result: DisposeResult) {
                        Log.d(MainActivity.TAG, "dispose finish")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "dispose start")
                    }
                })
                // final common callback
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })
        }

        comb_take.setOnClickListener {

            CoCo.with(this)
                .take(createSDCardFile())
                .then()
                .dispose()
//                .disposer(CustomDisposer())
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })

        }

        comb_pick.setOnClickListener {

            CoCo.with(this)
                .pick()
                .then()
                .dispose()
                //set if you want save the result to file
//                .fileToSaveResult(createSDCardFile())
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })
        }

        debug_for_default_disposer.setOnClickListener {
            startActivity(Intent(this, DebugDefaultDisposerActivity::class.java))
        }
    }

    /**
     * create a file from asset Path
     */
    private fun createSdImageFile(fileName: String) {
        return try {
            val inStream: InputStream = resources.assets.open("ic_launcher_round.png")
            val outStream = FileOutputStream(fileName, true)
            val buffer = ByteArray(1024)
            var len = -1
            while (inStream.read(buffer).also { len = it } != -1) {
                outStream.write(buffer, 0, len)
            }
            outStream.close()
            inStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * custom disposer
     * rotation image
     */
    class CustomDisposer : Disposer {
        override fun disposeFile(originPath: String, targetToSaveResult: File?): DisposeResult {
            return DisposeResult().also {
                var bitmap = QualityCompressor()
                    .compress(originPath, 80)
                val m = Matrix()
                m.postRotate(90f)
                bitmap = Bitmap.createBitmap(
                    bitmap!!, 0, 0, bitmap.width,
                    bitmap.height, m, true
                )
                it.savedFile = targetToSaveResult
                it.compressBitmap = bitmap
            }
        }

    }
}
