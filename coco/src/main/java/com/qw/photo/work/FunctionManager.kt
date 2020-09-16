package com.qw.photo.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.qw.photo.agent.AcceptActivityResultHandlerFactory
import com.qw.photo.agent.IContainer
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.functions.DisposeBuilder
import com.qw.photo.functions.PickBuilder
import com.qw.photo.functions.TakeBuilder
import java.io.File
import java.util.ArrayList

/**
 * Created by rocket on 2019/6/18.
 */
class FunctionManager(internal val container: IContainer) {

    internal val workerFlows = ArrayList<Any>()

    /**
     * 系统相机拍照
     * @param fileToSave 拍照完成后的存储路径，如果指定压缩，也是此路径（必传）
     */
    fun take(fileToSave: File): TakeBuilder =
        TakeBuilder(this).fileToSave(fileToSave)

    /**
     * 系统选择照片
     */
    fun pick(): PickBuilder =
        PickBuilder(this)

    /**
     * 处理图片（可自定义）
     */
    fun dispose(): DisposeBuilder = DisposeBuilder(this)
        //you can also custom disposer
        .disposer(DefaultImageDisposer.getDefault())

    companion object {
        internal fun create(activity: Activity) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(activity))

        internal fun create(fragment: Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

        internal fun create(fragment: android.app.Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

    }

}