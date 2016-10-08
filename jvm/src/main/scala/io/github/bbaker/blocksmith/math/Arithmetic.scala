package io.github.bbaker.blocksmith.math
import scala.math._
/**
  * @author Brandon Barker
  *         10/8/2016
  *         Copyright 2016. Subject to the Mozilla Public License 2.0.
  */
object Arithmetic {

  implicit class BSIntegral[A](val numb: A) extends AnyVal {
    def %+(divisor: A)(implicit iA: Integral[A]): A = {
      import iA._
      val modVal: A =  numb % divisor
      if (gteq(modVal, iA.zero)) modVal else modVal + divisor
    }
  }
}
