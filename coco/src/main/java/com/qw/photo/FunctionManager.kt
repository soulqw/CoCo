package com.qw.photo

import android.app.Activity
import com.qw.photo.fragment.FragmentFactory
import com.qw.photo.fragment.IWorker
import com.qw.photo.pojo.CaptureParams
import com.qw.photo.pojo.CaptureResult
import com.qw.photo.pojo.PickParams
import com.qw.photo.pojo.PickResult
import java.io.File


/**
 * @author cd5160866
 */
class FunctionManager(private val containerActivity: Activity) {

    /**
     * 拍照
     * @param targetFile 拍照完成后的存储路径，如果指定压缩，也是此路径（必传）
     */
    fun take(targetFile: File): CaptureParams<CaptureResult> {
        val mWorker: IWorker<CaptureResult> = FragmentFactory.create(containerActivity)
        val result = CaptureParams(mWorker)
        result.targetFile(targetFile)
        mWorker.setParams(result)
        mWorker.setResult(CaptureResult())
        return result
    }

    /**
     * 选择照片
     * @param targetFile 如果需要压缩之后的存贮路径
     */
    fun pick(targetFile: File? = null): PickParams<PickResult> {
        val mWorker: IWorker<PickResult> = FragmentFactory.create(containerActivity)
        val result = PickParams(mWorker)
        result.targetFile(targetFile)
        mWorker.setParams(result)
        mWorker.setResult(PickResult())
        return result
    }

}