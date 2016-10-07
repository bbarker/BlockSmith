// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

/**
  * Player represents the user in the Mycraft world. A Player is primarily a view
  * into the Mycraft world, and so much of the work is done by the Camera which
  * the Player owns. This class also manages a Player's movements and physics.
  *
  * @author Mitchell Kember
  * @since 09/12/2011
  */

class Player {

  /**
    * The number of units above this Player's feet that the head or Camera
    * is stationed.
    */
  private val CAMERA_HEIGHT: Float = 1.5f
  /**
    * Speed in units per 60 FPS frame for this Player's movement.
    */
  private val MOVE_SPEED: Float = 0.07f
  /**
    * The pull of gravity, in units per 60 FPS frame.
    */
  private val GRAVITY: Float = -0.005f
  /**
    * The initial upward velocity this Player will have upon jumping.
    */
  private val INITAL_JUMP_VELOCITY: Float = 0.11f
  /**
    * The view of this Player into the Mycraft world.
    */
  private val camera: Camera = new Camera
  /**
    * The height of what the Player is currently standing on.
    */
  private var ground: Float = 0
  /**
    * The height of this Player; this Player's Y coordinate in 3D space where
    * positive Y is upwards.
    */
  private var height: Float = 5
  /**
    * The vertical velocity of this Player, used for jumping and falling.
    */
  var velocity: Float = 0
  camera.setPositionY(height + CAMERA_HEIGHT)
  /**
    * Used for collision detection, to determine which direction this Player is moving.
    */
  private var deltaPosition: Vector = Vector()

  /**
    * Causes this Player to jump, unless this Player is already in the air
    * (jumping or falling) in which case nothing happens.
    */
  def jump() {
    if (height == ground) {
      ground = 0
      height += 0.0001f
      velocity = INITAL_JUMP_VELOCITY
    }
  }

  /**
    * Checks for collision with blocks and moves the Camera accordingly.
    * F
    *
    * @param chunk the Chunk this Player is in
    */
  def collision(chunk: Chunk) {
    // Boundaries (Y boundaries are handled by the jumping code in the move method).
    val position: Vector = camera.getPosition
    if (position.x < 0) camera.setPositionX(0)
    else if (position.x > 16) camera.setPositionX(16)
    if (position.z < 0) camera.setPositionZ(0)
    else if (position.z > 16) camera.setPositionZ(16)
    // Right and left
    if (deltaPosition.x > 0) {
      if (Math.round(position.x) < 16 && Math.round(position.x) > position.x && ((position.z - 0.25f >= 0 && chunk.getBlockType(Block(Math.round(position.x), height.toInt, (position.z - 0.25f).toInt)) != 0)
        || (position.z + 0.25f < 16 && chunk.getBlockType(Block(Math.round(position.x), height.toInt, (position.z + 0.25f).toInt)) != 0)
        || (height + 1 < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block(Math.round(position.x), (height + 1).toInt, (position.z - 0.25f).toInt)) != 0)
        || (height + 1 < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block(Math.round(position.x), (height + 1).toInt, (position.z + 0.25f).toInt)) != 0))
      ) {
        camera.setPositionX(Math.round(position.x) - 0.5f)
      }
    }
    else {
      if (Math.round(position.x) - 1 >= 0 && Math.round(position.x) < position.x && ((position.z - 0.25f >= 0 && chunk.getBlockType(Block(Math.round(position.x) - 1, height.toInt, (position.z - 0.25f).toInt)) != 0)
        || (position.z + 0.25f < 16 && chunk.getBlockType(Block(Math.round(position.x) - 1, height.toInt, (position.z + 0.25f).toInt)) != 0)
        || (height + 1 < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block(Math.round(position.x) - 1, (height + 1).toInt, (position.z - 0.25f).toInt)) != 0)
        || (height + 1 < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block(Math.round(position.x) - 1, (height + 1).toInt, (position.z + 0.25f).toInt)) != 0))
      ) {
        camera.setPositionX(Math.round(position.x) + 0.5f)
      }
    }
    // Forward and backward
    if (deltaPosition.z > 0) {
      if (Math.round(position.z) < 16 && Math.round(position.z) > position.z && ((position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, height.toInt, Math.round(position.z))) != 0)
        || (position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, height.toInt, Math.round(position.z))) != 0)
        || (height + 1 < 16 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, (height + 1).toInt, Math.round(position.z))) != 0)
        || (height + 1 < 16 && position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, (height + 1).toInt, Math.round(position.z))) != 0))
      ) {
        camera.setPositionZ(Math.ceil(position.z).toInt - 0.5f)
      }
    }
    else {
      if (Math.round(position.z) - 1 >= 0 && Math.round(position.z) < position.z && ((position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, height.toInt, Math.round(position.z) - 1)) != 0)
        || (position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, height.toInt, Math.round(position.z) - 1)) != 0)
        || (height + 1 < 16 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, (height + 1).toInt, Math.round(position.z) - 1)) != 0)
        || (height + 1 < 16 && position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, (height + 1).toInt, Math.round(position.z) - 1)) != 0))
      ) {
        camera.setPositionZ(Math.round(position.z) + 0.5f)
      }
    }
    // Falling
    if (deltaPosition.y <= 0) {
      var drop: Int = height.toInt
      // Cast down a line until it reaches a solid block, which is the ground.
      while (drop >= 1 && !((position.x.toInt < 16 && position.z.toInt < 16 && chunk.getBlockType(Block((position.x).toInt, drop - 1, (position.z).toInt)) != 0)
        || (position.z.toInt < 16 && position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, drop - 1, (position.z).toInt)) != 0)
        || (position.x.toInt < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x).toInt, drop - 1, (position.z + 0.25f).toInt)) != 0)
        || (position.x + 0.25f < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, drop - 1, (position.z + 0.25f).toInt)) != 0)
        || (position.z.toInt < 16 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, drop - 1, (position.z).toInt)) != 0)
        || (position.x.toInt < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block((position.x).toInt, drop - 1, (position.z - 0.25f).toInt)) != 0)
        || (position.z - 0.25f >= 0 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, drop - 1, (position.z - 0.25f).toInt)) != 0)
        || (position.x + 0.25f < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block((position.x + 0.25f).toInt, drop - 1, (position.z - 0.25f).toInt)) != 0)
        || (position.x - 0.25f >= 0 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x - 0.25f).toInt, drop - 1, (position.z + 0.25f).toInt)) != 0))) {
        drop -= 1
      }
      ground = drop
    }
    else {
      // Hitting your head when jumping
      if (Math.round(position.y) < 16 && ((position.x.toInt < 16 && position.z.toInt < 16 && chunk.getBlockType(Block((position.x).toInt, Math.round(position.y), (position.z).toInt)) != 0)
        || (position.z.toInt < 16 && position.x + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, Math.round(position.y), (position.z).toInt)) != 0)
        || (position.x.toInt < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x).toInt, Math.round(position.y), (position.z + 0.25f).toInt)) != 0)
        || (position.x + 0.25f < 16 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x + 0.25f).toInt, Math.round(position.y), (position.z + 0.25f).toInt)) != 0)
        || (position.z.toInt < 16 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, Math.round(position.y), (position.z).toInt)) != 0)
        || (position.x.toInt < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block((position.x).toInt, Math.round(position.y), (position.z - 0.25f).toInt)) != 0)
        || (position.z - 0.25f >= 0 && position.x - 0.25f >= 0 && chunk.getBlockType(Block((position.x - 0.25f).toInt, Math.round(position.y), (position.z - 0.25f).toInt)) != 0)
        || (position.x + 0.25f < 16 && position.z - 0.25f >= 0 && chunk.getBlockType(Block((position.x + 0.25f).toInt, Math.round(position.y), (position.z - 0.25f).toInt)) != 0)
        || (position.x - 0.25f >= 0 && position.z + 0.25f < 16 && chunk.getBlockType(Block((position.x - 0.25f).toInt, Math.round(position.y), (position.z + 0.25f).toInt)) != 0))
      ) {
        // Reposition and stop upward velocity
        height = Math.ceil(position.y).toInt - CAMERA_HEIGHT - 0.5f
        velocity = 0
      }
    }
  }

  /**
    * Moves this Player and orients this Player's view according to user input.
    *
    * @param input the user input
    * @param multiplier
    */
  def move(input: GameStateInputData, multiplier: Float) {
    val previousPosition: Vector = camera.getPosition
    // Movement
    if (input.forward) {
      camera.moveForward(MOVE_SPEED * multiplier)
    }
    if (input.backward) {
      camera.moveForward(-MOVE_SPEED * multiplier)
    }
    if (input.left) {
      camera.strafeRight(-MOVE_SPEED * multiplier)
    }
    if (input.right) {
      camera.strafeRight(MOVE_SPEED * multiplier)
    }
    if (height != ground) {
      height += velocity * multiplier
      velocity += GRAVITY * multiplier
      if (height < ground) {
        height = ground
        velocity = 0
      }
      else if (height + CAMERA_HEIGHT > 16) {
        height = 16 - CAMERA_HEIGHT
        velocity = 0
      }
      camera.setPositionY(height + CAMERA_HEIGHT)
    }
    // Calculate the delta position
    deltaPosition = camera.getPosition.minus(previousPosition)
    // Orient the camera
    camera.pitch(input.lookDeltaY)
    camera.yaw(input.lookDeltaX)
  }

  /**
    * Gets this Player's Camera object.
    *
    * @return the camera
    */
  def getCamera: Camera = camera

}

object Player {

}
