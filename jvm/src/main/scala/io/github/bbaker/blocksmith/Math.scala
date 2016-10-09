package io.github.bbaker.blocksmith

/**
  * @author Brandon Barker
  *         10/8/2016
  *         Copyright 2016. Subject to the Mozilla Public License 2.0.
  */
object Math {

  implicit class BSIntegral[A](val numb: A) extends AnyVal {
    def %+(divisor: A)(implicit iA: Integral[A]): A = {
      val modVal: A = iA.rem(numb, divisor)
      if (iA.compare(modVal, iA.zero) >= 0) modVal
      else iA.plus(modVal, divisor)
    }
  }
}
