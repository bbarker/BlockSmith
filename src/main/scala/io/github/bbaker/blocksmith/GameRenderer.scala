// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import java.io.IOException
import java.nio.{BufferOverflowException, IntBuffer}
import java.util.logging.Level

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl._
import org.lwjgl.util.glu.GLU._
import org.lwjgl.{BufferUtils, LWJGLException}
import org.newdawn.slick.opengl.{Texture, TextureLoader}
import org.newdawn.slick.util.ResourceLoader

/**
  * GameRenderer is responsible for managing the application's window and
  * rendering an instance of GameState into it.
  *
  * Note: sometimes the Java VM will crash when placing a block. It doesn't happen
  * often, but I have no idea why this happens.
  *
  * @author Mitchell Kember
  * @since 07/12/2011
  */

import io.github.bbaker.blocksmith.GameRenderer._

/**
  * Creates a new GameRenderer and sets up the LWJGL window.
  *
  * @throws LWJGLException if there is an error setting up the window
  */
final class GameRenderer @throws[LWJGLException]() extends GameStateListener {

  /**
    * The furthest away from this Camera that objects will be rendered.
    */
  private val renderDistance: Float = 50

  /**
    * The ID for the Vertex Buffer Object (VBO).
    */
  private var bufferObjectID: Int = 0

  /**
    * A simple 16 by 16 dirt texture.
    */
  private var dirtTexture: Texture = null

  /**
    * The number of vertices last uploaded to the VBO.
    */
  private var numVerts: Int = 0

  //Display.setResizable(true)
  Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT) )
  Display.setFullscreen(false)
  Display.setTitle(WINDOW_TITLE)
  // Try using oversampling for smooth edges.
  try {
    Display.create (new PixelFormat(0, DEPTH_BUFFER_BITS, 0, DESIRED_SAMPLES) )
  }
  catch {
    case lwjgle: LWJGLException =>
      // Replace this with text on screen
      println("Could not enable anti-aliasing. Brace yourself for jaggies.")
      Display.create (new PixelFormat(0, DEPTH_BUFFER_BITS, 0, 0) )
  }

  // Get ready
  prepareOpenGL()
  resizeOpenGL()
  initializeData()
  loadTextures()

  /**
    * Enables and Disables various OpenGL states. This should be called once when
    * the GameRenderer is created, before any rendering.
    */
  private def prepareOpenGL() = {
    glEnable (GL_CULL_FACE) // back face culling
    glEnable (GL_DEPTH_TEST) // z-buffer
    glEnable (GL_TEXTURE_2D) // textures
    // We don't need these
    glDisable (GL_ALPHA_TEST)
    glDisable (GL_STENCIL_TEST)
    glDisable (GL_DITHER)
    glDisable (GL_LIGHTING)
    // Cross hair and selected block hilighting
    glLineWidth (2.0f)
    glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    // Background colour
    glClearColor (0.8f, 0.9f, 1.0f, 0.0f)
  }

  /**
    * Resizes the OpenGL viewport and recalculates the projection matrix.
    */
  def resizeOpenGL() = {
    glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective(45, DISPLAY_WIDTH.toFloat / DISPLAY_HEIGHT.toFloat, 0.25f, renderDistance)
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
  }

  /**
    * Testing updates to resize
    */
  def testResizeOpenGL() = {
    glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT)
//    glMatrixMode(GL_PROJECTION)
//    glLoadIdentity()
//    gluPerspective(45, DISPLAY_WIDTH.toFloat / DISPLAY_HEIGHT.toFloat, 0.25f, renderDistance)
//    glMatrixMode(GL_MODELVIEW)
//    glLoadIdentity()
  }


  /**
    * Renders a GameState.
    *
    * @param state the GameState to render
    */
  def render(state: GameState) = {
    // Clear colour and z buffers
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    // Load the identity matrix
    glLoadIdentity ()
    //
    //FIXME: this block isn't working
//    if (Display.wasResized()) {
//      GameRenderer.DISPLAY_WIDTH = Display.getWidth
//      GameRenderer.DISPLAY_HEIGHT = Display.getHeight
//      testResizeOpenGL()
//    }
    // Let the Camera calculate the view matrix
    state.getPlayerView.updateMatrix ()
    // Full brightness for textures
    glColor3b(127.toByte, 127.toByte, 127.toByte)
    // Start at 1 to avoid drawing 1st degenerate vertex and messing everything else up
    glDrawArrays (GL_TRIANGLE_STRIP, 1, numVerts)
    // Black lines
    glColor3b((-127).toByte, (-127).toByte, (-127).toByte)
    // Draw selected block outline hilight
    if (state.isBlockSelected) {
      val selectedBlock: Vector = GameRenderer.openGLCoordinatesForBlock (state.getSelectedBlock)
      // Just use immediate mode/fixed function pipeline
      glBegin (GL_LINE_STRIP)
      glVertex3f (selectedBlock.x, selectedBlock.y, selectedBlock.z)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y, selectedBlock.z)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y + 1, selectedBlock.z)
      glVertex3f (selectedBlock.x, selectedBlock.y + 1, selectedBlock.z)
      glVertex3f (selectedBlock.x, selectedBlock.y, selectedBlock.z)
      glVertex3f (selectedBlock.x, selectedBlock.y, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y + 1, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x, selectedBlock.y + 1, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x, selectedBlock.y, selectedBlock.z - 1)
      glEnd ()
      glBegin (GL_LINES)
      glVertex3f (selectedBlock.x, selectedBlock.y + 1, selectedBlock.z)
      glVertex3f (selectedBlock.x, selectedBlock.y + 1, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y + 1, selectedBlock.z)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y + 1, selectedBlock.z - 1)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y, selectedBlock.z)
      glVertex3f (selectedBlock.x + 1, selectedBlock.y, selectedBlock.z - 1)
      glEnd ()
    }
    // Reload identity matrix
    glLoadIdentity ()
    // Draw crosshair
    glBegin (GL_LINES)
    glVertex3f (- CROSSHAIR_SIZE / 2, 0, - 0.25f)
    glVertex3f (CROSSHAIR_SIZE / 2, 0, - 0.25f)
    glVertex3f (0, - CROSSHAIR_SIZE / 2, - 0.25f)
    glVertex3f (0, CROSSHAIR_SIZE / 2, - 0.25f)
    glEnd ()
    // Update
    Display.update ()
    Display.sync (60)
  }

  /**
    * Loads textures that will be used by this GameRenderer.
    */
  private def loadTextures() = {
    try {
      dirtTexture = TextureLoader.getTexture ("PNG", ResourceLoader.getResourceAsStream ("res/dirt.png") )
    }
    catch {
      case ioe: IOException =>
      BlockSmith.LOGGER.log (Level.WARNING, ioe.toString, ioe)
    }
    // Texture parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    // Minecraft! (try using GL_LINEAR and you'll see what I mean):
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    // It will stay bound
    dirtTexture.bind()
  }

  /**
    * Creates the VBO that this GameRenderer will use.
    *
    * @throws LWJGLException if VBOs are not supported
    */
  @throws[LWJGLException]
  private def initializeData () = {
    if (! GLContext.getCapabilities.GL_ARB_vertex_buffer_object) {
      BlockSmith.LOGGER.log (Level.SEVERE, "GL_ARB_vertex_buffer_object not supported.")
      throw new LWJGLException ("GL_ARB_vertex_buffer_object not supported")
    }
    // Create it
    bufferObjectID = ARBBufferObject.glGenBuffersARB
    // Vertex Data interleaved format: XYZST
    val position: Int = 3
    val texcoords: Int = 2
    val sizeOfInt: Int = 4 // 4 bytes in an int
    val vertexDataSize: Int = (position + texcoords) * sizeOfInt
    // Bind it
      ARBBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID)
    // Vertex and texture pointers
    glVertexPointer (3, GL_INT, vertexDataSize, 0)
    glTexCoordPointer (2, GL_INT, vertexDataSize, position * sizeOfInt)
    glEnableClientState (GL_VERTEX_ARRAY)
    glEnableClientState (GL_TEXTURE_COORD_ARRAY)
  }

  /**
    * Calculates vertices for a cube located at ({@code x}, {@code y}, {@code z}).
    * The vertices must be rendered with GL_TRIANGLE_STRIP.
    *
    * @param x the x-coordinate
    * @param y the y-coordinate
    * @param z the z-coordinate
    * @return the vertices in interleaved XYZST format
    */
  private def cubeData(x: Int, y: Int, z: Int): Array[Int] = Array[Int](
    // 23*5 ints
    x, y, z, 0, 0, // degenerate

    x, y, z, 1, 0,
    x + 1, y, z, 1, 1,

    x, y + 1, z, 0, 0,
    x + 1, y + 1, z, 0, 1,

    x, y + 1, z - 1, 1, 0,
    x + 1, y + 1, z - 1, 1, 1,

    x, y, z - 1, 0, 0,
    x + 1, y, z - 1, 0, 1,

    x, y, z, 1, 0,
    x + 1, y, z, 1, 1,

    x + 1, y, z, 0, 0, // degenerate
    x + 1, y, z, 0, 0, // degenerate

    x + 1, y, z, 0, 1,
    x + 1, y, z - 1, 1, 1,

    x + 1, y + 1, z, 0, 0,
    x + 1, y + 1, z - 1, 1, 0,

    x + 1, y + 1, z - 1, 0, 0, // degenerate
    x, y + 1, z - 1, 0, 0, // degenerate

    x, y + 1, z - 1, 0, 0,
    x, y, z - 1, 0, 1,

    x, y + 1, z, 1, 0,
    x, y, z, 1, 1,

    x, y, z, 0, 0  // degenerate
  )


  /**
    * Updates the VBO when the a {@code chunk} in the GameState has changed.
    *
    * @param chunk the chunk that has changed
    */
  override def gameStateChunkChanged(chunk: Chunk) = {
    val data: Array[Array[Array[Byte]]] = chunk.getData

    def putCubeData(bufSize: Int): IntBuffer = {
      val vertexData: IntBuffer = BufferUtils.createIntBuffer(bufSize)
      for {
        xx <- 0 until 16
        yy <- 0 until 16
        zz <- 0 until -16 by -1
      } yield {
        if (data(xx)(yy)(-zz) !=0 ) {
          vertexData.put(cubeData(xx, yy, zz))
        }
      }
      vertexData
    }

    val vertexData: IntBuffer = try {
      putCubeData(70000)
    } catch {
      case boe1: BufferOverflowException => try {
        putCubeData(150000)
      } catch {
        case boe2: BufferOverflowException =>
          // Bail out
          System.out.println ("Oops! Mycraft has crashed!")
          BlockSmith.LOGGER.log (Level.SEVERE, boe2.toString, boe2)
          System.exit(1)
          BufferUtils.createIntBuffer(0) // Never reached
      }
    }
    numVerts = vertexData.position / 5
    vertexData.flip
    // Upload data
    ARBBufferObject.glBufferDataARB(
      ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexData,
      ARBBufferObject.GL_DYNAMIC_DRAW_ARB
    )
  }

}

object GameRenderer {
  var DISPLAY_WIDTH: Int = 1600
  var DISPLAY_HEIGHT: Int = 1200
  private val DEPTH_BUFFER_BITS: Int = 24
  private val DESIRED_SAMPLES: Int = 8
  private val WINDOW_TITLE: String = "BlockSmith"
  /**
    * The width and height of the cross hairs in the middle of the screen.
    */
  private val CROSSHAIR_SIZE: Float = 0.025f

  /**
    * Gets the vertices to use for rendering a block (inverts the z axis).
    *
    * @param block the block's location
    * @return its rendering coordinates
    */
  def openGLCoordinatesForBlock(block: Block): Vector =
  Vector(block.x, block.y, -block.z)
}