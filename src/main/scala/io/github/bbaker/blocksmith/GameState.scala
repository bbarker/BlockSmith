// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import scala.util.control.Breaks._

/**
  * GameState is the model in the Model-View-Controller (MVC) design architecture
  * for this application. GameState is responsible for simulating the Mycraft world.
  * It is a snapshot of a Mycraft world at any given time.
  *
  * @author Mitchell Kember
  * @since 07/12/2011
  */


/**
  * Creates a new GameState with the specified class implementing
  * GamStateListener to listen for state changes.
  *
  * @param listener the object to receive state change events (usually the renderer)
  */
final class GameState (var listener: GameStateListener) {
  /**
    * The one and only Player.
    */
  private val player: Player = new Player
  /**
    * The one and only Chunk... so far.
    */
  private val chunk: Chunk = new Chunk
  /**
    * The currently selected block.
    */
  private var selectedBlock: Block = null
  /**
    * The block of air which will be replaced with a solid block if the Player
    * chooses to.
    */
  private var newBlock: Block = null

  /**
    * The length of the Player's arm; how far away from the Player a block
    * can be placed.
    */
  private val ARM_LENGTH: Float = 6

  listener.gameStateChunkChanged(chunk)


  /**
    * Updates the GameState, responding to user input through {@code GameStateInputData}.
    * This should be called every frame.
    *
    * @param input     user input that should modify the state or move the player
    * @param deltaTime time passed since the last call in milliseconds
    * @see GameStateInputData
    */
  def update(input: GameStateInputData, deltaTime: Float) {
    // Everything is simulated to look correct at 60FPS, and is multiplied
    // by this to match the real framerate.
    val multiplier: Float = deltaTime / (100.0f / 6.0f)
    // Player movement
    //println(player.velocity) // FIXME, DEBUG: this shows we are moving but can't see blocks
    player.move(input, multiplier)
    player.collision(chunk)
    if (input.jump) player.jump()
    // Set selectedBlock and newBlock
    calculateSelectedBlock(chunk)
    // Break or place a block
    if (selectedBlock != null && newBlock != null) {
      if (input.breakBlock) {
        chunk.setBlockType(selectedBlock, 0.toByte)
        // Notify the listener
        listener.gameStateChunkChanged(chunk)
      }
      else if (input.placeBlock) {
        chunk.setBlockType(newBlock, 1.toByte)
        // Notify the listener
        listener.gameStateChunkChanged(chunk)
      }
    }
  }

  /**
    * Calculates {@code selectedBlock} and {@code newBlock}.
    *
    * @param chunk the chunk the Player is in
    */
  def calculateSelectedBlock(chunk: Chunk) {
    val position: Vector = player.getCamera.getPosition
    val sight: Vector = player.getCamera.getSight
    var ray: Vector = null // Vector cast out from the players position to find a block
    var step: Vector = null // step to increment ray by
    // Blocks are null unless they become assigned.
    selectedBlock = null
    newBlock = null
    // The following works, and is bug-free. That is all.
    // XY plane (front and back faces)
    // Start out assuming the front/back block is very far away so other blocks
    // will be chosen first, if there is no block found (if z == 0 or the ray leaves
    // its confines.
    var frontBackDistSquared: Float = Float.MaxValue
    if (sight.z != 0) {
      // Calculate ray and step depending on look direction
      if (sight.z > 0) ray = position.plus(sight.scaled((Math.ceil(position.z) - position.z).asInstanceOf[Float] / sight.z))
      else ray = position.plus(sight.scaled((Math.floor(position.z) - position.z).asInstanceOf[Float] / sight.z))
      step = sight.scaled(Math.abs(1.0f / sight.z))
      // Do the first step already if z == 16 to prevent an ArrayIndexOutOfBoundsException
      if (ray.z == 16) ray.add(step)
      breakable {
        while (ray.x >= 0 && ray.x < 16 && ray.y >= 0 && ray.y < 16 && ray.z >= 0 && ray.z < 16) {

          // Give up if we've extended the ray longer than the Player's arm length
          val distSquared: Float = ray.minus(position).magnitudeSquared
          if (distSquared > ARM_LENGTH * ARM_LENGTH) break //todo: remove break
          if (sight.z > 0) {
            if (chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])) != 0) {
              selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])
              if (selectedBlock.z - 1 >= 0) {
                newBlock = Block(selectedBlock.x, selectedBlock.y, selectedBlock.z - 1)
                if (chunk.getBlockType(newBlock) != 0) newBlock = null
              }
              frontBackDistSquared = distSquared
              break //todo: remove break
            }
          }
          else {
            if (ray.z - 1 >= 0 && chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int] - 1)) != 0) {
              selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int] - 1)
              if (selectedBlock.z + 1 < 16) {
                newBlock = Block(selectedBlock.x, selectedBlock.y, selectedBlock.z + 1)
                if (chunk.getBlockType(newBlock) != 0) newBlock = null
              }
              frontBackDistSquared = distSquared
              break //todo: remove break
            }
          }
          ray.add(step)
        }
      }
    }
    // YZ plane (left and right faces)
    var leftRightDistSquared: Float = Float.MaxValue
    if (sight.x != 0) {
      if (sight.x > 0) ray = position.plus(sight.scaled((Math.ceil(position.x) - position.x).asInstanceOf[Float] / sight.x))
      else ray = position.plus(sight.scaled((Math.floor(position.x) - position.x).asInstanceOf[Float] / sight.x))
      step = sight.scaled(Math.abs(1.0f / sight.x))
      if (ray.x == 16) ray.add(step)
      breakable {
        while (ray.x >= 0 && ray.x < 16 && ray.y >= 0 && ray.y < 16 && ray.z >= 0 && ray.z < 16) {
          val distSquared: Float = ray.minus(position).magnitudeSquared
          if (distSquared > ARM_LENGTH * ARM_LENGTH || distSquared > frontBackDistSquared) //break //todo: break is not supported
            if (sight.x > 0) {
              if (chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])) != 0) {
                selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])
                if (selectedBlock.x - 1 >= 0) {
                  newBlock = Block(selectedBlock.x - 1, selectedBlock.y, selectedBlock.z)
                  if (chunk.getBlockType(newBlock) != 0) newBlock = null
                }
                leftRightDistSquared = distSquared
                break //todo: remove break
              }
            }
            else {
              if (ray.x - 1 >= 0 && chunk.getBlockType(Block(ray.x.asInstanceOf[Int] - 1, ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])) != 0) {
                selectedBlock = Block(ray.x.asInstanceOf[Int] - 1, ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])
                if (selectedBlock.x + 1 < 16) {
                  newBlock = Block(selectedBlock.x + 1, selectedBlock.y, selectedBlock.z)
                  if (chunk.getBlockType(newBlock) != 0) newBlock = null
                }
                leftRightDistSquared = distSquared
                break //todo: remove break
              }
            }
          ray.add(step)

        }
      }
    }
    // XZ plane (bottom and top faces)
    var bottomTopDistSquared: Float = Float.MaxValue
    if (sight.y != 0) {
      if (sight.y > 0) ray = position.plus(sight.scaled((Math.ceil(position.y) - position.y).asInstanceOf[Float] / sight.y))
      else ray = position.plus(sight.scaled((Math.floor(position.y) - position.y).asInstanceOf[Float] / sight.y))
      step = sight.scaled(Math.abs(1.0f / sight.y))
      if (ray.y == 16) ray.add(step)
      breakable {
        while (ray.x >= 0 && ray.x < 16 && ray.y >= 0 && ray.y < 16 && ray.z >= 0 && ray.z < 16) {
          val distSquared: Float = ray.minus(position).magnitudeSquared
          if (distSquared > ARM_LENGTH * ARM_LENGTH ||
            distSquared > frontBackDistSquared ||
            distSquared > leftRightDistSquared
          ) break //todo: remove break
            if (sight.y > 0) {
              if (chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])) != 0) {
                selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])
                if (selectedBlock.y - 1 >= 0) {
                  newBlock = Block(selectedBlock.x, selectedBlock.y - 1, selectedBlock.z)
                  if (chunk.getBlockType(newBlock) != 0) newBlock = null
                }
                bottomTopDistSquared = distSquared
                break //todo: remove break
              }
            }
            else {
              if (ray.y - 1 >= 0 && chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int] - 1, ray.z.asInstanceOf[Int])) != 0) {
                selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int] - 1, ray.z.asInstanceOf[Int])
                if (selectedBlock.y + 1 < 16) {
                  newBlock = Block(selectedBlock.x, selectedBlock.y + 1, selectedBlock.z)
                  if (chunk.getBlockType(newBlock) != 0) newBlock = null
                }
                bottomTopDistSquared = distSquared
                break //todo: remove break
              }
            }
          ray.add(step)
        }
      }
    }
  }

  /**
    * Determines whether a block is currently selected or not.
    *
    * @return true if a block is selected
    */
  def isBlockSelected: Boolean = selectedBlock != null


  /**
    * Gets the currently selected block.
    *
    * @return the block which is selected
    */
  def getSelectedBlock: Block = selectedBlock


  /**
    * Gets the Player's Camera object.
    *
    * @return the Player's Camera
    */
  def getPlayerView: Camera = player.getCamera

}