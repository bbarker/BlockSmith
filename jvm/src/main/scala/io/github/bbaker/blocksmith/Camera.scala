// Copyright 2012 Mitchell Kember. Subject to the MIT License.
// Copyright 2012 Mitchell Kember. Subject to the MIT License.
package io.github.bbaker.blocksmith

import org.lwjgl.util.glu.GLU.gluLookAt

/**
  * Camera manages a first person camera in 3D space. It calculates the necessary
  * matrix transformations to orient the camera, and provides a simplified
  * mechanism for orienting the camera via movement and rotation methods. It makes
  * heavy use of the Vector class.
  * <p>
  * Camera is meant to be used with OpenGL, which uses a right-handed system.
  * Camera, however, creates a transparent layer between this and functions as
  * if it was using a left-handed coordinate system (into the screen being
  * positive Z rather than negative). GameState uses a left-handed system in the
  * sense that objects only exist in positive coordinates extending forward, up
  * and right from the origin. This is by virtue of the fact that coordinates of
  * objects (which are integers) are simply stored as indices in a 3D array.
  * Because Camera's methods cater to the state's system, Camera is a part of the
  * model (MVC), not the view. This decision was made through the logic that
  * Camera represents a property of the Player (its view) rather than an aspect
  * of rendering. Internally, however, Camera is more of a mix of the model and
  * the view.
  *
  * @author Mitchell Kember
  * @since 08/12/2011
  * @see Vector
  */
object Camera {
  /**
    * Constant used  to convert from degrees to radians.
    */
  private val DEG_TO_RAD: Float = Math.PI.toFloat / 180.0f
  /**
    * This Vector will always point skyward. There is no need for a changing
    * "up" vector because the horizon should always be level.
    */
  private val sky: Vector = Vector(0, 1, 0)
}

final class Camera {
  /**
    * The position, stored internally in OpenGL/right-handed coordinates. That is,
    * movement actions will change the Z-coordinate of {@code position} inversely.
    */
  private val position: Vector = Vector(0, 0, 0)
  /**
    * Normalized Vector pointing to the right of this Camera.
    */
  private var right: Vector = Vector(1, 0, 0)
  /**
    * The view or sight of this Camera, as a normalized Vector relative to
    * this Camera's position.
    *
    * @see #position
    */
  private var sight: Vector = Vector(0, 0, -1)
  /**
    * Keeps track of this Camera's pitch, used to avoid pitching below
    * -90 degrees or above +90 degrees.
    *
    * @see #pitch
    */
  private var rotationX: Float = 0

  /**
    * Updates the OpenGL ModelView matrix stack for this Camera's view.
    * Call after all Camera transformations and before rendering. It is
    * assumed that the identity matrix has been loaded.
    */
  def updateMatrix() {
    // Get the absolute coordinate of the view direction
    val lookAt: Vector = position.plus(sight)
    // Multiply onto the matrix stack
    gluLookAt(
      position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z,
      Camera.sky.x, Camera.sky.y, Camera.sky.z
    )
  }

  /**
    * Moves this Camera by adding {@code vec} to its position.
    *
    * @param vec the movement Vector
    */
  def move(vec: Vector) {
    position.add(vec.invertedZ)
  }

  /**
    * Moves this Camera forward in the direction it is facing. Pass a negative
    * {@code distance} to move backwards. This will never move the camera along
    * the global Y axis, only along the global XZ plane.
    *
    * @param distance the distance to move forward by
    */
  def moveForward(distance: Float) {
    position.add(Vector(sight.x, 0, sight.z).normalized.scaled(distance))
  }

  /**
    * Moves this Camera to the right while facing the same direction. Pass a
    * negative {@code distance} to move to the left. This will never move the
    * camera along the global Y axis, only along the global XZ plane.
    *
    * @param distance the distance to move to the right by
    */
  def strafeRight(distance: Float) {
    position.add(right.scaled(distance))
  }

  /**
    * Rotates this Camera about the X axis by {@code angle} degrees. This will
    * not pitch below -90 degrees or above +90 degrees.
    *
    * @param angle degrees to rotate by
    */
  def pitch(angle: Float) {
    if (rotationX + angle < -89.9f || rotationX + angle > 89.9f) return
    rotationX += angle // keep track of angle
    sight = Vector.axisRotation(sight, right, angle * Camera.DEG_TO_RAD)
  }

  /**
    * Rotates this Camera about the Y axis by {@code angle} degrees.
    *
    * Note: This does not rotate about the Y axis relative to this Camera,
    * but rather about the global Y axis so that the horizon will always
    * stay level through this Camera's view. Technically, this is more
    * of a "swivel" than a yaw.
    *
    * @param angle degrees to rotate by
    */
  def yaw(angle: Float) {
    sight = Vector.axisRotation(sight, Camera.sky, -angle * Camera.DEG_TO_RAD)
    right = Vector.cross(sight, Camera.sky).normalized
  }

  /**
    * Sets this Camera's position's X-coordinate to {@code x}.
    *
    * @param x the new position's X-coordinate
    */
  def setPositionX(x: Float) {
    position.x = x
  }

  /**
    * Sets this Camera's position's Y-coordinate to {@code y}.
    *
    * @param y the new position's Y-coordinate
    */
  def setPositionY(y: Float) {
    position.y = y
  }

  /**
    * Sets this Camera's position's Z-coordinate to {@code z}.
    *
    * @param z the new position's Z-coordinate
    */
  def setPositionZ(z: Float) {
    position.z = -z
  }

  /**
    * Gets a copy of this Camera's position.
    *
    * @return the position
    */
  def getPosition: Vector = {
    return position.invertedZ
  }

  /**
    * Gets a copy of the Vector which represents the direction of this Camera's line of sight.
    */
  def getSight: Vector = {
    return sight.invertedZ
  }
}