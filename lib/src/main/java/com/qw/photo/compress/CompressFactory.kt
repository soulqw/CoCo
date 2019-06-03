package com.qw.photo.compress

import com.qw.photo.constant.CompressStrategy


/**
 *
 * @author cd5160866
 */
object CompressFactory {

    fun create(strategy: CompressStrategy): ICompress {
        return when (strategy) {
            CompressStrategy.MATRIX -> {
                MatrixCompressor()
            }
            CompressStrategy.QUALITY -> {
                QualityCompressor()
            }
        }
    }
}