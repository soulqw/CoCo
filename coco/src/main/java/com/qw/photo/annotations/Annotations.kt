package com.qw.photo.annotations

import android.support.annotation.IntDef
import com.qw.photo.pojo.TakeParams.Companion.BACK
import com.qw.photo.pojo.TakeParams.Companion.FRONT


/**
 *
 * @author cd5160866
 */

@IntDef(FRONT, BACK)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraFace