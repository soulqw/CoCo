package com.qw.photo

class CoCoConfigs {


    /**
     *  set the default configs if you needed
     */
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