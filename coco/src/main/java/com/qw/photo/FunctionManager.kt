package com.qw.photo

import android.app.Activity
import com.qw.photo.constant.Action
import com.qw.photo.fragment.FragmentFactory
import com.qw.photo.fragment.IWorker
import com.qw.photo.pojo.*
import java.io.File


/**
 * @author cd5160866
 */
class FunctionManager(private val containerActivity: Activity) {

    /**
     * 拍照
     * @param targetFile 拍照完成后的存储路径
     */
    fun take(targetFile: File? = null): CaptureParams<CaptureResult> {
        val mWorker: IWorker<CaptureResult> = FragmentFactory.create(containerActivity)
        val result = CaptureParams(mWorker)
        result.targetFile(targetFile)
        mWorker.setParams(result)
        mWorker.setActions(Action.CAPTURE)
        return result
    }

    /**
     * 选择照片
     */
    fun pick(targetFile: File? = null): PickParams<PickResult> {
        val mWorker: IWorker<PickResult> = FragmentFactory.create(containerActivity)
        val result = PickParams(mWorker)
        result.targetFile(targetFile)
        mWorker.setParams(result)
        mWorker.setActions(Action.PICK)
        return result
    }

}