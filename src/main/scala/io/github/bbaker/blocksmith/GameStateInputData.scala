// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * A container for input that must be handled by GameState. This creates a layer
  * of abstraction between the actual means of input (which key, which mouse button,
  * etc.) and what type of input is actual required for GameState to perform
  * specific actions.
  *
  * @author Mitchell Kember
  * @since 09/12/2011
  */
object GameStateInputData {
  private val DEFAULT_LOOK_SENSITIVITY: Float = 1.0f / 10.0f
  private var lookSensitivity: Float = DEFAULT_LOOK_SENSITIVITY

  /**
    * Changes the look sensitivity. Smaller values will cause the view to pan
    * around more slowly; for example if the mouse is used, a very small
    * value would require lifting the mouse several times to turn around. Larger
    * cause the view to pan around more quickly.
    *
    * @param lookSensitivity the new look sensitivity
    */
  def setLookSensitivity(lookSensitivity: Float) {
    this.lookSensitivity = lookSensitivity * DEFAULT_LOOK_SENSITIVITY
  }
}

import io.github.bbaker.blocksmith.GameStateInputData._

/**
  * Creates a new GameStateInputData, initializing all fields.
  *
  * @param forward    if the Player should move forward
  * @param backward   if the Player should move backward
  * @param left       if the Player should move to the left
  * @param right      if the Player should move to the right
  * @param jump       if the Player should jump
  * @param lookDeltaX the distance along the x-axis the Player has shifted its gaze
  * @param lookDeltaY the distance along the y-axis the Player has shifted its gaze
  * @param cycleBlock how many times the Player should cycle the block being held (wraps around)
  * @param placeBlock if the Player should place a block
  */
final class GameStateInputData(
  val forward: Boolean,
  val backward: Boolean,
  val left: Boolean,
  val right: Boolean,
  val jump: Boolean,
  var lookDeltaX: Float,
  var lookDeltaY: Float,
  val cycleBlock: Int,
  var breakBlock: Boolean,
  var placeBlock: Boolean
) {
  this.lookDeltaX = lookDeltaX * lookSensitivity
  this.lookDeltaY = lookDeltaY * lookSensitivity
  // You can't break and place a block at the same time!
  if (breakBlock && placeBlock) {
    throw new IllegalArgumentException
  }
  this.breakBlock = breakBlock
  this.placeBlock = placeBlock
}