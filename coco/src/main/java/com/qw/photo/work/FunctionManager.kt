package com.qw.photo.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.qw.photo.agent.AcceptActivityResultHandlerFactory
import com.qw.photo.agent.IAcceptActivityResultHandler
import com.qw.photo.pojo.TakeParams
import com.qw.photo.pojo.PickParams
import java.io.File

/**
 * Created by rocket on 2019/6/18.
 */
class FunctionManager(private val handler: IAcceptActivityResultHandler) {

    /**
     * 拍照
     * @param targetFile 拍照完成后的存储路径，如果指定压缩，也是此路径（必传）
     */
    fun take(targetFile: File): TakeParams = TakeParams(TakePhotoWorker(handler)).targetFile(targetFile)

    /**
     * 选择照片
     * @param targetFile 如果需要压缩之后的存贮路径
     */
    fun pick(targetFile: File? = null): PickParams = PickParams(PickPhotoWorker(handler)).targetFile(targetFile)


    companion object {
        internal fun create(activity: Activity) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(activity))

        internal fun create(fragment: Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

        internal fun create(fragment: android.app.Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

    }
}