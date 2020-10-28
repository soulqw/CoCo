package com.qw.photo

import java.io.File

class CoCoConfigs {

    companion object Builder {

        internal var cropsResultFile: File? = null
            get() = field

        @JvmStatic
        fun configCropsResultFile(file: File) {
            this.cropsResultFile = file
        }

        @JvmStatic
        fun setDebug(isDebug: Boolean) {
            DevUtil.isDebug = isDebug
        }

    }

}