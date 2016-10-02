// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * Classes that listen and respond to changes in the GameState must implement
  * this interface. This allows them to be notified when Chunks are modified.
  *
  * @author Michell Kember
  * @since 10/12/2011
  */
trait GameStateListener {
  def gameStateChunkChanged(chunk: Chunk)
}