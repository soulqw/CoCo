package com.qw.photo

class CoCoConfigs {

    companion object Builder {

        internal var cropsResultFile: String? = null

        @JvmStatic
        fun configCropsResultFile(file: String) {
            this.cropsResultFile = file
        }

        @JvmStatic
        fun setDebug(isDebug: Boolean) {
            DevUtil.isDebug = isDebug
        }

    }

}