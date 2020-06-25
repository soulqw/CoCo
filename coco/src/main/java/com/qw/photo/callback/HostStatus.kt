package com.qw.photo.callback

import com.qw.photo.annotations.HostStatus

/**
 *
 * @author cd5160866
 * @date 2019-09-21
 */
interface Host {

    @HostStatus
    fun getStatus(): Int

    class Status {

        companion object {

            /**
             * 初始化状态
             */
            const val INIT = 0x1 shl 1

            /**
             * 可用态
             */
            const val LIVE = 0x1 shl 2

            /**
             * 结束态
             */
            const val DEAD = 0x1 shl 3

        }
    }

}