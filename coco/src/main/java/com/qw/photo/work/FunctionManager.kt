package com.qw.photo.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.qw.photo.agent.AcceptActivityResultHandlerFactory
import com.qw.photo.agent.IContainer
import com.qw.photo.functions.BaseFunctionBuilder
import com.qw.photo.functions.DisposeBuilder
import com.qw.photo.functions.PickBuilder
import com.qw.photo.functions.TakeBuilder
import com.qw.photo.pojo.BaseResult
import com.qw.photo.pojo.DisposeResult
import java.io.File
import java.util.ArrayList

/**
 * Created by rocket on 2019/6/18.
 */
class FunctionManager<BaseResult>(val container: IContainer) {

    internal val workerFlows = ArrayList<BaseFunctionBuilder<BaseResult>>()

    /**
     * 拍照
     * @param targetFile 拍照完成后的存储路径，如果指定压缩，也是此路径（必传）
     */
    fun take(targetFile: File): TakeBuilder =
        TakeBuilder(this, TakePhotoWorker(container)).targetFile(targetFile)

    /**
     * 选择照片
     * @param targetFile 如果需要压缩之后的存贮路径
     */
    fun pick(targetFile: File? = null): PickBuilder =
        PickBuilder(this, PickPhotoWorker(container)).targetFile(targetFile)

    fun dispose(): DisposeBuilder = DisposeBuilder(this)

    fun crop() {}

    companion object {
        internal fun create(activity: Activity) =
            FunctionManager<BaseResult>(AcceptActivityResultHandlerFactory.create(activity))

        internal fun create(fragment: Fragment) =
            FunctionManager<BaseResult>(AcceptActivityResultHandlerFactory.create(fragment))

        internal fun create(fragment: android.app.Fragment) =
            FunctionManager<BaseResult>(AcceptActivityResultHandlerFactory.create(fragment))

    }
}