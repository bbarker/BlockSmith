// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * Chunk represents a chunk of 16 by 16 by 16 blocks in the BlockSmith world.
  * Each block uses one byte to represent its type, totaling 4 kilobytes to
  * store the information for one Chunk.
  *
  * @author Mitchell Kember
  * @since 09/12/2011
  */

import Chunk._
import Math._
import io.github.bbaker.blocksmith.Coordinates.Region2d
final class Chunk private (xx: Int, zz:Int) {

  println(s"Added new chunk at $xx, $zz") // DEBUG

  /**
    * This 3D array stores all the types of the blocks in this Chunk. It is in
    * the following format:
    *
    * {@code data[x][y][z]}
    */
  private val data: Array[Array[Array[Byte]]] = Array.ofDim(width, depth, height)


  // Place a ground layer of blocks
  for {
    xx <- 0 until width
    zz <- 0 until depth
  } yield {
    data(xx)(0)(zz) = 1
  }


  /**
    * Gets this Chunk's data array.
    *
    * @return the data
    */
  def getData: Array[Array[Array[Byte]]] = data


  /**
    * Set a block's type.
    *
    * @param block the location of the block
    * @param blockType  its new type id
    */
  def setBlockType(block: Block, blockType: Byte) =
    data(block.x)(block.y)(block.z) = blockType

  /**
    * Get a block's type.
    *
    * @param block the location of the block.
    * @return its type id
    */
  def getBlockType(block: Block): Byte = {
    val xLocal  =  block.x %+ width
    val zLocal  =  block.z %+ depth
    val yLocal  =  block.y %+ height
    data(xLocal)(yLocal)(zLocal)
  }
}

object Chunk{
  val width = 16
  val depth = 16
  val height = 16

  def apply(region2d: Region2d) = new Chunk(region2d.xx, region2d.zz)
}