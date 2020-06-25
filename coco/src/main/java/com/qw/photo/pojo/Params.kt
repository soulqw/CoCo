package com.qw.photo.pojo

import com.qw.photo.DevUtil
import com.qw.photo.annotations.CameraFace
import com.qw.photo.annotations.PickRange
import com.qw.photo.constant.Constant
import com.qw.photo.dispose.disposer.DefaultImageDisposer
import com.qw.photo.dispose.disposer.ImageDisposer
import com.qw.photo.work.Executor
import com.qw.photo.work.IWorker
import java.io.File


/**
 * @author cd5160866
 */
open class BaseParams<Result : BaseResult>(internal val worker: IWorker<*, Result>) {

    internal var disposer: ImageDisposer? = null

    internal var file: File? = null

    /**
     * 应用参数为后续操作做准备
     * （不会做额外处理）
     */
    fun apply(): Executor<Result> {
        return Executor(this)
    }

    /**
     * 应用参数为后续操作做准备，并根据ImageDisposer 做图片处理
     * @see ImageDisposer
     */
    @JvmOverloads
    fun applyWithDispose(compressor: ImageDisposer = DefaultImageDisposer.getDefault()): Executor<Result> {
        this.disposer = compressor
        return apply()
    }
}

class TakeParams(worker: IWorker<TakeParams, TakeResult>) : BaseParams<TakeResult>(worker) {

    companion object {

        const val FRONT = 1

        const val BACK = 0

    }

    internal var cameraFace = BACK

    /**
     * 指定被最终写到的文件
     */
    fun targetFile(file: File): TakeParams {
        DevUtil.d(Constant.TAG, "capture: saveFilePath: " + (file.path ?: "originUri is null"))
        this.file = file
        return this
    }

    /**
     * 相机的方向，默认后摄像头
     * 这个方法不一定对所有手机生效
     * @see CameraFace
     */
    fun cameraFace(@CameraFace face: Int): TakeParams {
        this.cameraFace = face
        return this
    }
}

class PickParams(worker: IWorker<PickParams, PickResult>) : BaseParams<PickResult>(worker) {

    companion object {

        /**
         * 系统相册
         */
        const val PICK_DICM = 0

        /**
         * 全文件路径
         */
        const val PICK_CONTENT = 1

    }

    internal var pickRange = PICK_DICM

    fun targetFile(file: File?): PickParams {
        DevUtil.d(Constant.TAG, "pick: saveFilePath: " + (file?.path ?: "originUri is null"))
        this.file = file
        return this
    }

    /**
     * 选择范围根据你自己需要
     * @param pickRange 筛选范围
     * @see PICK_DICM
     * @see PICK_CONTENT
     */
    fun range(@PickRange pickRange: Int): PickParams {
        this.pickRange = pickRange
        return this
    }

}