package io.github.bbaker.blocksmith

import io.github.bbaker.blocksmith.Coordinates.Region2d

import scala.collection.mutable
/**
  * @author Brandon Barker
  *         10/8/2016
  *         Copyright 2016. Subject to the Mozilla Public License 2.0.
  */
class World()(implicit val listener: GameStateListener) {

  protected val chunkStore: mutable.Map[Region2d, Chunk] = mutable.Map()
  val hotChunkCoords: mutable.Set[Region2d] = mutable.Set()

  val initCoord = Region2d(0, 0)

  val startChunk = Chunk(initCoord)
  chunkStore(initCoord) = startChunk
  hotChunkCoords += initCoord

  def chunk(coords: Region2d) = chunkStore.getOrElse(coords, {
    val newChunk = Chunk(coords)
    println(s"new chunk at $coords") // DEBUG
    chunkStore(coords) = newChunk
    listener.gameStateChunkChanged(newChunk)
    newChunk
  })



}
