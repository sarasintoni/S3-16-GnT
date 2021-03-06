package com.unibo.s3.main_system.util

/**
  * An utility class containing math utility methods.
  *
  * @author mvenditto
  */
object GntMathUtils {

  def keepInRange(v: Float, min: Float, max: Float): Float =
    if (v < min) min else if(v > max) max else v
}

