package io.github.bbaker.blocksmith.math

import org.specs2._

import Arithmetic._


class ArithmeticSpec extends Specification { def is = s2"""

Testing basic arithmetic operations

    Testing non-negative modulus

    Small negative dividend check works    $testN1_5
    Larger negative dividend check works   $testN6_5
    Positive dividend check works          $test6_5
    0 dividend works                       $test0_5
    Emits division by zero                 $test6_0
 """

  //
  // Test positive module
  //
  val testN1_5 = -1 %+ 5 must_== 4
  val testN6_5 = -6 %+ 5 must_== 4
  val test6_5  =  6 %+ 5 must_== 1
  val test0_5  =  0 %+ 5 must_== 0
  val test6_0  =  6 %+ 0 must throwA[ArithmeticException](message = "/ by zero")




}
