package com.qw.photo.dispose.disposer

import com.qw.photo.pojo.DisposeResult
import java.io.File

/**
 *
 * @author: george
 * @date: 2020-01-05
 */
interface Disposer {

    /**
     * how to dispose your file, it will work on backGround thread
     *
     * @param originPath the origin file path to dispose
     * @param targetToSaveResult the file that you can write your result
     */
    fun disposeFile(originPath: String, targetToSaveResult: File?): DisposeResult

}