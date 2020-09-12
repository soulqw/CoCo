package com.qw.photo.functions

import com.qw.photo.annotations.PickRange
import com.qw.photo.callback.PickCallBack
import com.qw.photo.pojo.PickResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.PickPhotoWorker

class PickBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<PickBuilder, PickResult>(fm, PickPhotoWorker(fm.container)) {

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

    internal var pickCallBack: PickCallBack? = null

    fun callBack(callBack: PickCallBack): PickBuilder {
        this.pickCallBack = callBack
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

    override fun getParamsBuilder(): PickBuilder {
        return this
    }

}