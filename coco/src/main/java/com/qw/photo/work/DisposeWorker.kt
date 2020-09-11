package com.qw.photo.work

import com.qw.photo.agent.IContainer
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.pojo.DisposeResult

/**
 *Author: 思忆
 *Date: Created in 2020/9/8 3:29 PM
 */
class DisposeWorker(handler: IContainer) :Worker<DisposeResult> {
    override fun start(callBack: CoCoCallBack<DisposeResult>) {
    }

}