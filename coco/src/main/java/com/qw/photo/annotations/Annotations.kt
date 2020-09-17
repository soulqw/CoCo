package com.qw.photo.annotations

import androidx.annotation.IntDef
import com.qw.photo.constant.Face.BACK
import com.qw.photo.constant.Face.FRONT
import com.qw.photo.constant.Host.Status.Companion.INIT
import com.qw.photo.constant.Host.Status.Companion.LIVE
import com.qw.photo.constant.Host.Status.Companion.DEAD
import com.qw.photo.constant.Range.PICK_CONTENT
import com.qw.photo.constant.Range.PICK_DICM

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

@IntDef(INIT, LIVE, DEAD)
@Retention(AnnotationRetention.SOURCE)
annotation class HostStatus