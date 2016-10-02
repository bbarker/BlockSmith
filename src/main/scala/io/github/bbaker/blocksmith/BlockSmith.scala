// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import java.io.IOException
import java.util.logging.{FileHandler, Level, Logger}

import org.lwjgl.LWJGLException

/**
  * Mycraft is an open source java game that uses the LightWeight Java
  * Game Library (LWJGL). It is inspired by the popular game Minecraft.
  * It was written as a final summative project for the ICS2O course.
  *
  * "Minecraft" is an official trademark of Mojang AB. This work is not
  * formally related to, endorsed by or affiliated with Minecraft or Mojang AB.
  *
  * @author Mitchell Kember
  * @version 1.0 06/12/2011
  * @since 06/12/2011
  */
object BlockSmith {
  /**
    * Used to log errors to a log file.
    */
  val LOGGER: Logger = Logger.getLogger(this.getClass.getName)

  /**
    * The main method.
    *
    * @param args the command line arguments
    */
  def main(args: Array[String]) {
    var controller: GameController = null
    // Try creating the GameController
    // Any LWJGLExceptions that occur during the initialization of LWJGL
    // (Display, Keyboard, Mouse) will propagate up here and be caught.
    try {
      System.out.println("Mycraft is starting up.")
      controller = new GameController
      controller.run() // begin the main loop
    }
    catch {
      case lwjgle: LWJGLException =>
        LOGGER.log(Level.SEVERE, lwjgle.toString, lwjgle)
    } finally {
      if (controller != null) {
        // Clean up
        controller.destroy()
      }
    }
  }

  try {
    try {
      LOGGER.addHandler(new FileHandler("errors.log", true))
    }
    catch {
      case ioe: IOException =>
        LOGGER.log(Level.WARNING, ioe.toString, ioe)
    }
  }
}