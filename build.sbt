
name := "BlockSmith"

description := "BlockSmith is currently an experiment in the ways of MineCraft."

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

Seq(lwjglSettings: _*)

// Seems to be missing
//Seq(slickSettings: _*)

val sbtlwjglversion = "3.1.5"

libraryDependencies += "org.slick2d" % "slick2d-core" % "1.0.2"

mainClass in (Compile,run) := Some("io.github.bbaker.blocksmith.BlockSmith")
