// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith
import scala.math.{signum}

import Coordinates._
import Vector.vecProjToBlockProj1D

/**
  * GameState is the model in the Model-View-Controller (MVC) design architecture
  * for this application. GameState is responsible for simulating the Mycraft world.
  * It is a snapshot of a Mycraft world at any given time.
  *
  * @author Mitchell Kember
  * @since 07/12/2011
  *
  * @author Brandon Elam Barker
  * @since 10/15/2016
  *
  */


/**
  * Creates a new GameState with the specified class implementing
  * GamStateListener to listen for state changes.
  *
  */
final class GameState() {

  /**
    * the object to receive state change events (usually the renderer)
    */
  private var listener: GameStateListener = new GameStateListener {
    // Dummy listener
    override def gameStateChunkChanged(chunk: Chunk): Unit =
      println("GameState not set yet!")
  }
  /**
    * The one and only Player.
    */
  private val player: Player = new Player
  /**
    * The one and only World... so far.
    */
  val world: World = new World(listener)
  /**
    * The currently selected block.
    */
  private var selectedBlockOpt: Option[Block] = None
  /**
    * The block of air which will be replaced with a solid block if the Player
    * chooses to.
    */
  private var newBlockOpt: Option[Block] = None

  /**
    * The length of the Player's arm; how far away from the Player a block
    * can be placed.
    */
  private val ARM_LENGTH: Float = 6

  /**
    * @param listener the object to receive state change events (usually the renderer)
    */
  def setListener(listener: GameStateListener): Unit = {
    this.listener = listener
    world.setListener(listener)
  }



  //TODO: need to somehow render "dirty" chunks, but minimize state tracking. Do it in renderer?
  /**
    * Updates the GameState, responding to user input through {@code GameStateInputData}.
    * This should be called every frame.
    *
    * @param input     user input that should modify the state or move the player
    * @param deltaTime time passed since the last call in milliseconds
    * @see GameStateInputData
    */
  def update(input: GameStateInputData, deltaTime: Float): Unit = {
    // Everything is simulated to look correct at 60FPS, and is multiplied
    // by this to match the real framerate.
    val multiplier: Float = deltaTime / (100.0f / 6.0f)
    // Player movement
    player.move(input, multiplier)
    // println(s"player coords: ${player.coords2d.xx}, ${player.coords2d.zz}") // DEBUG
    val chunk = world.chunk(player.coords2d)
    player.collision(chunk)
    if (input.jump) player.jump()
    // Set selectedBlock and newBlock
    calculateSelectedBlock(chunk)
    // Break or place a block
    for {
      selectedBlock <- selectedBlockOpt
      newBlock <- newBlockOpt
    } {
      if (input.breakBlock) {
        chunk.setBlockType(selectedBlock, 0)
        println(s"removing block at ${selectedBlock.x}, ${selectedBlock.y}, ${selectedBlock.z} in chunk ${chunk.xx}, ${chunk.zz} (sight)") // DEBUG
        // Notify the listener
        listener.gameStateChunkChanged(chunk)
      }
      else if (input.placeBlock) {
        chunk.setBlockType(newBlock, 1)
        println(s"placing block at ${newBlock.x}, ${newBlock.y}, ${newBlock.z} in chunk ${chunk.xx}, ${chunk.zz} (sight)") // DEBUG
        // Notify the listener
        listener.gameStateChunkChanged(chunk)
      }
    }
  }

  //TODO: also need to check that we can select blocks in neighboring chunks
  /**
    * Calculates {@code selectedBlock} and {@code newBlock}.
    *
    * @param chunk the chunk the Player is in
    */
  def calculateSelectedBlock(chunk: Chunk) {
    val position: Vector = player.getCamera.getPosition
    val sight: Vector = player.getCamera.getSight
    selectedBlockOpt = None
    newBlockOpt = None
    // The following works, and is bug-free. That is all.
    // XY plane (front and back faces)
    // Start out assuming the front/back block is very far away so other blocks
    // will be chosen first, if there is no block found (if z == 0 or the ray leaves
    // its confines.
    var frontBackDistSquared: Float = Float.MaxValue
    var leftRightDistSquared: Float = Float.MaxValue
    var bottomTopDistSquared: Float = Float.MaxValue

    def twoFaceCheck(pj: VectorProj1D): Unit = {
      val step = sight.scaled(Math.abs(1.0f / pj(sight)))

      // TODO: we are doing this based on the Y  template
      if (pj(sight) != 0) {
        val xInd: Int = if (pj == VectorX) 1 else 0
        val yInd: Int = if (pj == VectorY) 1 else 0
        val zInd: Int = if (pj == VectorZ) 1 else 0

        // Vector cast out from the players position to find a block
        val rayInit: Vector =
          if (pj(sight) > 0) position.plus(sight.scaled((Math.ceil(pj(position)) - pj(position)).asInstanceOf[Float] / pj(sight)))
          else position.plus(sight.scaled((Math.floor(pj(position)) - pj(position)).asInstanceOf[Float] / pj(sight)))
        if (pj(rayInit) % 16 == 0) rayInit.add(step)
        //TODO: WHY? A: if nothing else, seems to cause texturing issues near some borders, but need to understand better

        def distSquared(ray: Vector): Float = ray.minus(position).magnitudeSquared
        def rayDistMaxReached(ray: Vector): Boolean = {
          val dSq = distSquared(ray)
          pj match {
            case VectorX =>
              dSq > ARM_LENGTH * ARM_LENGTH ||
                dSq > frontBackDistSquared
            case VectorY =>
              dSq > ARM_LENGTH * ARM_LENGTH ||
                dSq > frontBackDistSquared ||
                dSq > leftRightDistSquared
            case VectorZ =>
              dSq > ARM_LENGTH * ARM_LENGTH
          }
        }
        val updateDistSq: Float => Unit = pj match {
          case VectorX => (dSq: Float) => {leftRightDistSquared = dSq}
          case VectorY => (dSq: Float) => {bottomTopDistSquared = dSq}
          case VectorZ => (dSq: Float) => {frontBackDistSquared = dSq}
        }

        // step to increment ray by
        lazy val rays: Stream[Vector] = rayInit #:: rays.map(_ + step)
        (for (ray <- rays.takeWhile(!rayDistMaxReached(_)))
        yield {
          if (pj(sight) > 0) {
            if (chunk.getBlockType(Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])) != 0) {
              val selectedBlock = Block(ray.x.asInstanceOf[Int], ray.y.asInstanceOf[Int], ray.z.asInstanceOf[Int])
              selectedBlockOpt = Some(selectedBlock)
              if (pj(selectedBlock) - 1 >= 0) {
                //println(s"selected new block E for pj ${pj.getClass} in chunk ${chunk.xx}, ${chunk.zz} (sight)") // DEBUG
                val newBlock = Block(selectedBlock.x - xInd, selectedBlock.y - yInd, selectedBlock.z - zInd)
                newBlockOpt = Some(newBlock)
                if (chunk.getBlockType(newBlock) != 0) newBlockOpt = None
              }
              updateDistSq(distSquared(ray))
            }
          }
          else {
            val tentativeBlock = Block(
              //FIXME: understand/doc why we need: - (if (chunk.xx < 0) 1 else 0), etc.
              ray.x.asInstanceOf[Int] - xInd - (if (chunk.xx < 0) 1 else 0),
              ray.y.asInstanceOf[Int] - yInd,
              ray.z.asInstanceOf[Int] - zInd - (if (chunk.zz < 0) 1 else 0)
            )
            if (pj(ray) - 1 >= 0 &&
              chunk.getBlockType(tentativeBlock) != 0) {
              val selectedBlock = tentativeBlock
              selectedBlockOpt = Some(selectedBlock)
              if (pj(selectedBlock) + 1 < 16) {
                //println(s"selected new block F for ${pj.getClass} in chunk ${chunk.xx}, ${chunk.zz} (ray)") // DEBUG
                val newBlock = Block(selectedBlock.x + xInd, selectedBlock.y + yInd, selectedBlock.z + zInd)
                newBlockOpt = Some(newBlock)
                if (chunk.getBlockType(newBlock) != 0) newBlockOpt = None
              }
              updateDistSq(distSquared(ray))
            }
          }
        }).toList
      }
    }

    // TODO: Note that having a selected block does NOT mean we have selected the
    // correct block and can immediately terminate. However, this implies that we could
    // rearrange the logic to be efficient and (possibly) simpler to follow

    // XY plane (left and right faces); frontBackDistSquared
    if (sight.z != 0) twoFaceCheck(VectorZ)
    // YZ plane (left and right faces); leftRightDistSquared
    if (sight.x != 0) twoFaceCheck(VectorX)
    // XZ plane (bottom and top faces); bottomTopDistSquared
    if (sight.y != 0) twoFaceCheck(VectorY)
  }



  /**
    * Determines whether a block is currently selected or not.
    *
    * @return true if a block is selected
    */
  def isBlockSelected: Boolean = selectedBlockOpt.nonEmpty


  /**
    * Gets the currently selected block.
    *
    * @return the block which is selected
    */
  def getSelectedBlock: Block = selectedBlockOpt.get


  /**
    * Gets the Player's Camera object.
    *
    * @return the Player's Camera
    */
  def getPlayerView: Camera = player.getCamera

}