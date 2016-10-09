package io.github.bbaker.blocksmith

import io.github.bbaker.blocksmith.Coordinates.Region2d

import scala.collection.mutable
/**
  * @author Brandon Barker
  *         10/8/2016
  *         Copyright 2016. Subject to the Mozilla Public License 2.0.
  */
class World(var listener: GameStateListener) {

  protected val chunkStore: mutable.Map[Region2d, Chunk] = mutable.Map()
  protected val chunkIdStore: mutable.Map[Region2d, Long] = mutable.Map()
  protected var nextChunkId: Long = 0

  val hotChunkCoords: mutable.Set[Region2d] = mutable.Set()

  val initCoord = Region2d(0, 0)

  val startChunk = Chunk(initCoord, this)
  chunkStore(initCoord) = startChunk
  hotChunkCoords += initCoord

  def chunk(coords: Region2d) = chunkStore.getOrElse(coords, {
    val newChunk = Chunk(coords, this)
    println(s"new chunk at $coords") // DEBUG
    chunkStore(coords) = newChunk
    listener.gameStateChunkChanged(newChunk)
    newChunk
  })

  def chunkId(coords: Region2d): Option[Long] = chunkIdStore.get(coords)

  //TODO: make this a critical section with e.g. reactors.io
  def setChunkId(chunk: Chunk): Unit = {
    chunkIdStore(chunk.region2d) = nextChunkId
    nextChunkId += 1
  }

  /**
    * @param listener the object to receive state change events (usually the renderer)
    */
  def setListener(listener: GameStateListener): Unit = {
    this.listener = listener
    listener.gameStateChunkChanged(startChunk)
  }


}
