package com.qw.photo.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.qw.photo.agent.AcceptActivityResultHandlerFactory
import com.qw.photo.agent.IContainer
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.Disposer
import com.qw.photo.functions.CropBuilder
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
     * select a photo from system gallery or file system
     */
    fun pick(): PickBuilder =
        PickBuilder(this)

    /**
     * dispose an file in background thread ,and will bind the lifecycle with current container
     * @param disposer you can also custom disposer
     * @see Disposer
     */
    @JvmOverloads
    fun dispose(disposer: Disposer = DefaultImageDisposer.get()): DisposeBuilder =
        DisposeBuilder(this)
            .disposer(disposer)

    /**
     * crop image by system crop function
     * @param afterCropFile File after crop file you want
     * @param cropWidth Int Int the crop width and height you want
     * @param cropHeight Int Int the crop width and height you want
     * @param originFile File? default is null if you just want crop pick or take result?, null is ok more see [Result]
     * @return CropBuilder
     */
    @JvmOverloads
    fun crop(afterCropFile : File,cropWidth:Int = 500,cropHeight:Int = 500,originFile : File? = null):CropBuilder = CropBuilder(this).afterCropFile(afterCropFile).cropSize(cropWidth,cropHeight).file(originFile)

    companion object {
        internal fun create(activity: Activity) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(activity))

        internal fun create(fragment: Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

        internal fun create(fragment: android.app.Fragment) =
            FunctionManager(AcceptActivityResultHandlerFactory.create(fragment))

    }

}