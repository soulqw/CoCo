package com.qw.photo.work

import com.qw.photo.agent.IContainer

/**
 *Author: 思忆
 *Date: Created in 2020/9/12 11:51 AM
 */
abstract class BaseWorker<Builder, ResultData>(container: IContainer) : Worker<Builder, ResultData> {
    val iContainer = container
}