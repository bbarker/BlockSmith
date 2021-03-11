package io.github.bbaker.blocksmith

/**
  * @author Brandon Barker
  *         10/8/2016
  *         Copyright 2016. Subject to the Mozilla Public License 2.0.
  */

object Coordinates {

  implicit def coords2dToRegion2d(coords: Coords2d): Region2d = Region2d(
    coords.xx / Chunk.width - (if (coords.xx < 0) 1 else 0),
    coords.zz / Chunk.depth - (if (coords.zz < 0) 1 else 0)
  )

  case class Coords2d(xx: Int, zz: Int)

  case class Region2d(xx: Int, zz: Int)

}