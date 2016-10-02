// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * Chunk represents a chunk of 16 by 16 by 16 blocks in the Mycraft world.
  * Each block uses one byte to represent its type, totaling 4 kilobytes to
  * store the information for one Chunk.
  *
  * @author Mitchell Kember
  * @since 09/12/2011
  */
final class Chunk {
  /**
    * This 3D array stores all the types of the blocks in this Chunk. It is in
    * the following format:
    *
    * {@code data[x][y][z]}
    */
  private val data: Array[Array[Array[Byte]]] = Array.ofDim(16, 16, 16)

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
    * @param type  its new type id
    */
  def setBlockType(block: Block, `type`: Byte) =
    data(block.x)(block.y)(block.z) = `type`

  /**
    * Get a block's type.
    *
    * @param block the location of the block.
    * @return its type id
    */
  def getBlockType(block: Block): Byte = data(block.x)(block.y)(block.z)

}