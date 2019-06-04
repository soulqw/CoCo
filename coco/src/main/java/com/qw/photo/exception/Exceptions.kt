package com.qw.photo.exception

import java.lang.IllegalStateException


/**
 *
 * @author cd5160866
 */
class CompressFailedException(override val message: String) : IllegalStateException()

class MissCompressStrategyException():IllegalStateException("compress image must have a strategy ")