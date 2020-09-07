package com.qw.photo.functions

import com.qw.photo.DevUtil
import com.qw.photo.annotations.PickRange
import com.qw.photo.constant.Constant
import com.qw.photo.pojo.PickResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.IWorker
import java.io.File

class PickBuilder(fm: FunctionManager, worker: IWorker<PickBuilder, PickResult>) :
    BaseFunctionBuilder<PickResult>(fm, worker) {

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

    fun targetFile(file: File?): PickBuilder {
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
    fun range(@PickRange pickRange: Int): PickBuilder {
        this.pickRange = pickRange
        return this
    }

}