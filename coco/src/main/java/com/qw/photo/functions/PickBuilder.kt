package com.qw.photo.functions

import com.qw.photo.annotations.FileType
import com.qw.photo.annotations.PickRange
import com.qw.photo.callback.PickCallBack
import com.qw.photo.constant.Type
import com.qw.photo.constant.Range
import com.qw.photo.pojo.PickResult
import com.qw.photo.work.FunctionManager
import com.qw.photo.work.PickPhotoWorker
import com.qw.photo.work.Worker

class PickBuilder(fm: FunctionManager) :
    BaseFunctionBuilder<PickBuilder, PickResult>(fm) {

    internal var pickRange = Range.PICK_DICM

    internal var fileType = Type.ALL

    internal var pickCallBack: PickCallBack? = null

    fun callBack(callBack: PickCallBack): PickBuilder {
        this.pickCallBack = callBack
        return this
    }

    /**
     *
     * @param pickRange the range you can choose
     * @see Range.PICK_DICM  the system gallery
     * @see Range.PICK_CONTENT the system content file
     *
     * if you choose the  PICK_CONTENT, you can also use type to file the type you want
     */
    fun range(@PickRange pickRange: Int = Range.PICK_DICM): PickBuilder {
        this.pickRange = pickRange
        return this
    }

    /**
     * set the file type to be choose from file system when use in
     * @see Range.PICK_CONTENT
     * @param type the file type to be filter
     */
    fun type(@FileType type: Int = Type.ALL): PickBuilder {
        this.fileType = type
        return this
    }

    override fun getParamsBuilder(): PickBuilder {
        return this
    }

    override fun generateWorker(builder: PickBuilder): Worker<PickBuilder, PickResult> {
        return PickPhotoWorker(functionManager.container, builder)
    }

}