package com.qw.photo.annotations

import androidx.annotation.IntDef
import com.qw.photo.constant.Host.Status.Companion.INIT
import com.qw.photo.constant.Host.Status.Companion.LIVE
import com.qw.photo.constant.Host.Status.Companion.DEAD
import com.qw.photo.functions.PickBuilder.Companion.PICK_CONTENT
import com.qw.photo.functions.PickBuilder.Companion.PICK_DICM
import com.qw.photo.functions.TakeBuilder.Companion.BACK
import com.qw.photo.functions.TakeBuilder.Companion.FRONT


/**
 *
 * @author cd5160866
 */

@IntDef(FRONT, BACK)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraFace

@IntDef(PICK_CONTENT, PICK_DICM)
@Retention(AnnotationRetention.SOURCE)
annotation class PickRange

@IntDef(INIT, LIVE,DEAD)
@Retention(AnnotationRetention.SOURCE)
annotation class HostStatus