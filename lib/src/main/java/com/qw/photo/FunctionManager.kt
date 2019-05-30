package com.qw.photo

import android.app.Activity
import com.qw.photo.fragment.FragmentFactory
import com.qw.photo.fragment.IWorker
import com.qw.photo.pojo.Action
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.PickParams
import java.io.File


/**
 * @author cd5160866
 */
class FunctionManager(containerActivity: Activity) {

    private val mWorker: IWorker = FragmentFactory.create(containerActivity)

    /**
     * 拍照
     * @param targetFile 拍照完成后的存储路径
     */
    fun take(targetFile: File? = null): CaptureParams {
        val result = CaptureParams(mWorker)
        result.targetFile(targetFile)
        mWorker.setParams(result)
        mWorker.setActions(Action.CAPTURE)
        return result
    }

    /**
     * 选择照片
     */
    fun pick(): PickParams {
        val result = PickParams(mWorker)
        mWorker.setParams(result)
        mWorker.setActions(Action.PICK)
        return result
    }

}