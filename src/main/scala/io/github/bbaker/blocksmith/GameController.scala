// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import org.lwjgl.input.{Cursor, Keyboard, Mouse}
import org.lwjgl.opengl.{Display, GL11}
import org.lwjgl.{BufferUtils, LWJGLException, Sys}



/**
  * GameController is the controller in the Model-View-Controller (MVC) design
  * architecture for this application. GameController handles user input and mediates
  * between the GameState and GameRenderer classes. It also manages the run loop
  * of Mycraft.
  *
  * @author Mitchell Kember
  * @since 07/12/2011
  * @see GameState
  * @see GameRenderer
  */

import io.github.bbaker.blocksmith.GameController._

/**
  * Creates a new GameController, which manages its own GameState and
  * GameRenderer, as well as user input (LWJGL Keyboard and Mouse).
  *
  * @throws LWJGLException if there was an error loading any part of LWJGL
  */
@throws[LWJGLException]
final class GameController() {


  /**
    * The heart of the game, the GameState.
    */
  private implicit val state: GameState = new GameState()

  /**
    * The renderer for this GameController's state.
    */
  private val renderer: GameRenderer = new GameRenderer()
  //TODO: use proper observable
  state.setListener(renderer)

  /**
    * Used for detecting space bar presses.
    */
  var wasSpaceBarDown: Boolean = false

  /**
    * Used for detecting left mouse clicks.
    */
  private var wasLeftMouseButtonDown: Boolean = false

  /**
    * Used for detecting right mouse clicks.
    */
  private var wasRightMouseButtonDown: Boolean = false

  /**
    * Used for calculating delta time between frames.
    */
  private var previousTime: Double = 0.0


  Keyboard.create ()
  val emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null)
  // This will make the mouse invisible. It will be "grabbed" by the
  // window, making it invisible and unable to leave the window.
  // Note for the above comment: i have set setGrabbed to false. At least on windows,
  // I can't use the keyboard and mouse when setGrabbed is true. We can use emptyCursor
  // to hide the cursor, but this is a double edged sword for gameplay in windowed mode.
  Mouse.setGrabbed (false)
  //ruMouse.setNativeCursor(emptyCursor)
  Mouse.create ()


  /**
    * Clean up LWJGL components.
    */
  def destroy () {
    // Methods already check if created before destroying.
    Mouse.destroy ()
    Keyboard.destroy ()
    Display.destroy ()
  }

  /**
    * Gets the time in milliseconds since this method was last called. If it
    * is greater than MAX_DELTA_TIME, that will be returned instead.
    *
    * @return the time since this method was last called in milliseconds
    * @see #MAX_DELTA_TIME
    */
  private def getDeltaTime: Float = {
    // Get hires time in milliseconds
    val newTime: Double = (Sys.getTime * 1000.0) / Sys.getTimerResolution
    // Calculate the delta
    val delta: Float = (newTime - previousTime).toFloat
    // New becomes old for next call
    previousTime = newTime
    // Return the delta time unless it's bigger than MAX_DELTA_TIME
    if (delta < MAX_DELTA_TIME) delta
    else MAX_DELTA_TIME
  }

  /**
    * Determines whether the mouse {@code button} has been clicked or not.
    *
    * @param button which mouse button to check
    * @return true if it is down and it wasn't last time this method was called
    */
  private def wasMouseClicked (button: MouseButton): Boolean = {
    val buttonDown: Boolean = Mouse.isButtonDown(button.ordinal)
    var clicked: Boolean = false
    // Determine if the mouse button wasn't down before but is now
    if (button eq Left) {
      clicked = ! wasLeftMouseButtonDown && buttonDown
      wasLeftMouseButtonDown = buttonDown
    }
    else if (button eq Right) {
      clicked = ! wasRightMouseButtonDown && buttonDown
      wasRightMouseButtonDown = buttonDown
    }
    clicked
  }

  /**
    * Determines whether the space bar key was pressed.
    *
    * @return true if the space bar was not pressed last time this was called but is now
    */
  private def wasSpaceBarPressed: Boolean = {
    val spaceBarDown: Boolean = Keyboard.isKeyDown (Keyboard.KEY_SPACE)
    val wasPressed: Boolean = ! wasSpaceBarDown && spaceBarDown
    wasSpaceBarDown = spaceBarDown
    wasPressed
  }

  /**
    * The run loop. The application will stay inside this method until the window
    * is closed or the Escape key is pressed.
    */
  def run () = {
    while (! Display.isCloseRequested && ! Keyboard.isKeyDown (Keyboard.KEY_ESCAPE) ) {
      if (Display.isVisible) {
        // Update the state with the required input
        state.update (new GameStateInputData(
          Keyboard.isKeyDown (Keyboard.KEY_W),
          Keyboard.isKeyDown (Keyboard.KEY_S),
          Keyboard.isKeyDown (Keyboard.KEY_A),
          Keyboard.isKeyDown (Keyboard.KEY_D),
          wasSpaceBarPressed,
          Mouse.getDX, Mouse.getDY, Mouse.getDWheel / - 120,
          wasMouseClicked(Left),
          wasMouseClicked (Right)),
          // Using the delta time for a framerate-independent simulation
          // should be the correct way to do things but it produces strange
          // results on the school computers, so instead simulate one
          // sixtieth of a second every frame.
          /*getDeltaTime()*/ 1000.0f / 60.0f
        )
        // Render it
        renderer.render(state)
      }
      else {
        // Only render if it needs rendering
        if (Display.isDirty) {
          renderer.render(state)
        }
        try {
          // If the window isn't visible, sleep a bit so that we're
          // not wasting resources by checking nonstop.
          Thread.sleep (100)
        }
        catch {
          case e: InterruptedException => ()
        }
      }
    }
  }

}


object GameController {

  /**
    * A tiny enum for identifying mouse buttons.
    *
    * @author Mitchell Kember
    * @since 10/12/2011
    */
  sealed trait MouseButton {val ordinal: Int}
  case object Left extends MouseButton() {val ordinal: Int = 0}
  case object Right extends MouseButton() {val ordinal: Int = 1}


  //  class MouseButton extends Enumeration {
  //    type MouseButton = Value
  //    val LEFT, RIGHT = Value
  //  }


  /**
    * The maximum amount of time to simulate over a single frame, in milliseconds.
    */
  private val MAX_DELTA_TIME: Float = 50.0f
}