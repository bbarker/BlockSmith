// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import io.github.bbaker.blocksmith.Coordinates.Coords2d

/**
  * Vector represents a three-dimensional (3D) vector. In particular, it provides
  * two of most operations, one mutating the current Vector and another returning
  * a new one (e.g. add/plus, sub/minus, scale/scaled). It also provides a
  * convenient method for axis rotations.
  *
  * @author Mitchell Kember
  * @since 08/12/2011
  */
object Vector {

  def apply() = new Vector()
  def apply(xx: Float, yy: Float, zz: Float) = new Vector(xx, yy, zz)


  /**
    * Calculates the cross product of {@code u} and {@code v} and returns
    * the result in a new Vector.
    *
    * @param u a Vector
    * @param v another Vector
    * @return the cross product
    */
  def cross(u: Vector, v: Vector): Vector = {
    new Vector(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x)
  }

  /**
    * Rotates {@code vec} about an arbitrary {@code axis} by {@code angle}
    * radians. This does not modify {@code vec}.
    *
    * @param vec   the Vector to rotate
    * @param axis  the arbitrary axis to rotate about
    * @param angle the angle in radians to rotate
    * @return the rotated Vector
    */
  def axisRotation(vec: Vector, axis: Vector, angle: Float): Vector = {
    val nAxis: Vector = axis.normalized
    val c: Float = Math.cos(angle).toFloat
    val s: Float = Math.sin(angle).toFloat
    new Vector(
      nAxis.x * (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z) * (1.0f - c) +
        vec.x * c + (-nAxis.z * vec.y + nAxis.y * vec.z) * s,
      nAxis.y * (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z) * (1.0f - c) + vec.y * c +
        (nAxis.z * vec.x - nAxis.x * vec.z) * s,
      nAxis.z * (nAxis.x * vec.x + nAxis.y * vec.y + nAxis.z * vec.z) * (1.0f - c) + vec.z * c +
        (-nAxis.y * vec.x + nAxis.x * vec.y) * s
    )
  }

  //TODO: Temporary, remove when Block extends Vector generically
  implicit def vecProjToBlockProj1D(vPj: VectorProj1D): BlockProj1D = vPj match {
    case VectorX => BlockX
    case VectorY => BlockY
    case VectorZ => BlockZ
  }

}

//
// Projection objects
//
sealed trait VectorProj
sealed trait VectorProj1D extends VectorProj {
  def apply(vec: Vector): Float
}
case object VectorX extends VectorProj1D {
  def apply(vec: Vector): Float = vec.x
}
object VectorY extends VectorProj1D {
  def apply(vec: Vector): Float = vec.y
}
object VectorZ extends VectorProj1D {
  def apply(vec: Vector): Float = vec.z
}



final class Vector(/**
     * The X component of this Vector.
     */
   var x: Float = 0.0f,

   /**
     * The Y component of this Vector.
     */
   var y: Float = 0.0f,

   /**
     * The Z component of this Vector.
     */
   var z: Float = 0.0f) {


  /**
    * Calculates the magnitude (length) of this Vector.
    *
    * @return the length of this Vector
    */
  def magnitude: Float = {
    Math.sqrt((x * x) + (y * y) + z * z).toFloat
  }

  /**
    * Calculates the magnitude (length) squared of this Vector. This is less
    * expensive to call than {@code magnitude}, because it skips the square root
    * operation. Use this when, for example, sorting by Vector length and the
    * actual magnitude is not required.
    *
    * @return the magnitude squared
    */
  def magnitudeSquared: Float = {
    (x * x) + (y * y) + (z * z)
  }

  /**
    * Adds {@code vec} to this Vector by adding each component
    * separately.
    *
    * @param vec the addend
    */
  def add(vec: Vector) {
    x += vec.x
    y += vec.y
    z += vec.z
  }

  def +(vec: Vector): Vector = Vector(x + vec.x, y + vec.y, z + vec.z)

  /**
    * Subtracts {@code vec} from this Vector by subtracting each
    * component separately.
    *
    * @param vec the subtrahend
    */
  def sub(vec: Vector) {
    x -= vec.x
    y -= vec.y
    z -= vec.z
  }

  /**
    * Scales this vector by the scalar value {@code s} by multiplying
    * each component separately.
    *
    * @param s the multiplier
    */
  def scale(s: Float) {
    x *= s
    y *= s
    z *= s
  }

  /**
    * Calculates the dot product of this Vector with {@code vec}.
    *
    * @param vec another Vector
    * @return the dot product
    */
  def dot(vec: Vector): Float = {
    x * vec.x + y * vec.y + z * vec.z
  }

  /**
    * Normalizes this Vector (i.e., makes its magnitude equal to 1).
    */
  def normalize(): Unit =
    if (magnitude != 0) {
      x /= magnitude
      y /= magnitude
      z /= magnitude
    }


  /**
    * Calculates the sum of this Vector and {@code vec}. This does not
    * modify this Vector.
    *
    * @param vec the addend
    * @return the sum
    */
  def plus(vec: Vector): Vector = {
    new Vector(x + vec.x, y + vec.y, z + vec.z)
  }

  /**
    * Calculates the difference of this Vector and {@code vec}. This does not
    * modify this Vector.
    *
    * @param vec the subtrahend
    * @return the difference
    */
  def minus(vec: Vector): Vector = {
    new Vector(x - vec.x, y - vec.y, z - vec.z)
  }

  /**
    * Calculates this vector scaled by the scalar value {@code s}. This
    * does not modify this Vector.
    *
    * @param s the multiplier
    * @return the scaled Vector
    */
  def scaled(s: Float): Vector = {
    new Vector(x * s, y * s, z * s)
  }

  /**
    * Calculates the normalized version of this Vector. This does not
    * modify this Vector.
    *
    * @return the unit Vector
    */
  def normalized: Vector = {
    val mag: Float = magnitude
    if (mag == 0) Vector()
    else new Vector(x / mag, y / mag, z / mag)
  }

  /**
    * Returns a copy of this Vector with its Z-axis inverted. Useful for converting
    * between a left-handed system and a right-handed system.
    *
    * @return the Vector
    */
  def invertedZ: Vector = {
    new Vector(x, y, -z)
  }

  def xzProj = Coords2d(x.toInt, z.toInt)
}