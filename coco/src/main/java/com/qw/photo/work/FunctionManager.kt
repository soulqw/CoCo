package com.qw.photo.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.qw.photo.agent.AcceptActivityResultHandlerFactory
import com.qw.photo.agent.IContainer
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
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
     * take photo from system camera
     * @param fileToSave the result of take photo to save
     */
    fun take(fileToSave: File): TakeBuilder =
        TakeBuilder(this).fileToSave(fileToSave)

    /**
     * select a photo from system gallery of file system
     */
    fun pick(): PickBuilder =
        PickBuilder(this)

    /**
     * dispose an file in background thread ,and will bind the lifecycle with current container
     * @param disposer you can also custom disposer
     * @see ImageDisposer
     */
    @JvmOverloads
    fun dispose(disposer: ImageDisposer = DefaultImageDisposer.getDefault()): DisposeBuilder =
        DisposeBuilder(this)
            .disposer(disposer)

    companion object {
        internal fun create(activity: Activity) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(activity))

        internal fun create(fragment: Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

        internal fun create(fragment: android.app.Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

    }

}