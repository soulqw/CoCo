package com.qw.photo.functions

import com.qw.photo.annotations.PickRange
import com.qw.photo.callback.PickCallBack
import com.qw.photo.constant.Range
import com.qw.photo.pojo.PickResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.PickPhotoWorker
import com.qw.photo.work.Worker

class PickBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<PickBuilder, PickResult>(fm) {

    internal var pickRange = Range.PICK_DICM

    internal var pickCallBack: PickCallBack? = null

    fun callBack(callBack: PickCallBack): PickBuilder {
        this.pickCallBack = callBack
        return this
    }

    /**
     * 选择范围根据你自己需要
     * @param pickRange 筛选范围
     * @see Range.PICK_DICM
     * @see Range.PICK_CONTENT
     */
    fun range(@PickRange pickRange: Int): PickBuilder {
        this.pickRange = pickRange
        return this
    }

    override fun getParamsBuilder(): PickBuilder {
        return this
    }

    override fun generateWorker(builder: PickBuilder): Worker<PickBuilder, PickResult> {
        return PickPhotoWorker(functionManager.container, builder)
    }

}