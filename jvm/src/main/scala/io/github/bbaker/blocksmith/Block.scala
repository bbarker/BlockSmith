// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * Block represents a single block in the Blockcraft world. Nothing more than a
  * container for the x, y and z indices.
  *
  * @author Mitchell Kember
  */
//TODO: add coords3D
case class Block (x: Int, y: Int, z: Int)

//TODO: refactor to extend vector after vector is made a case class
//TODO: the following should be remoable at that point
//
// Projection objects
//
sealed trait BlockProj
sealed trait BlockProj1D extends BlockProj {
  def apply(vec: Block): Int
}
case object BlockX extends BlockProj1D {
  def apply(vec: Block): Int = vec.x
}
object BlockY extends BlockProj1D {
  def apply(vec: Block): Int = vec.y
}
object BlockZ extends BlockProj1D {
  def apply(vec: Block): Int = vec.z
}



