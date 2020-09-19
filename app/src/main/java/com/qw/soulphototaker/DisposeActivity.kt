package com.qw.soulphototaker

import android.os.Bundle
import android.util.Log
import com.qw.photo.CoCo
import com.qw.photo.Utils
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.callback.TakeCallBack
import com.qw.photo.constant.Face
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.DisposeResult
import com.qw.photo.pojo.TakeResult
import kotlinx.android.synthetic.main.activity_take_photo.*

/**
 * @author cd5160866
 */
class DisposeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispose)
        setSupportActionBar(toolbar)
        title = "DisposeDetail"

    }
}
