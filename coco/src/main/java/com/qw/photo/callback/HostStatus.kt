package com.qw.photo.callback


/**
 *
 * @author cd5160866
 * @date 2019-09-21
 */
interface Host {

    fun getStatus(): Status

    enum class Status {
        /**
         * 初始化状态
         */
        INIT,

        /**
         * 可用态
         */
        LIVE,

        /**
         * 结束态
         */
        DEAD

    }

}