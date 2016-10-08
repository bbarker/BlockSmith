import LWJGLPlugin._

name := "BlockSmith root project"

scalaVersion in ThisBuild := "2.11.8"

val slickVersion = "1.0.2"
val lwjglVersion = "2.9.3"

// Seems to be missing
//Seq(slickSettings: _*)


lazy val root = project.in(file(".")).
  aggregate(BlockSmithJS, BlockSmithJVM).settings(
    publish := {},
    publishLocal := {}
  )

// Do `project BlockSmithJVM` in sbt to switch to JVM, then do `run`
mainClass in (Compile,run) := Some("io.github.bbaker.blocksmith.BlockSmith")

lazy val BlockSmith = crossProject.in(file(".")).
  configs(IntegrationTest).
  settings(Defaults.itSettings: _*).
  settings(
    name := "BlockSmith",
    description := "BlockSmith is currently an experiment in the ways of MineCraft.",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.8.5" % "it,test"
    )
  ).
  jvmSettings(
    // Add JVM-specific settings here
      libraryDependencies ++= Seq(
      "org.slick2d" % "slick2d-core" % slickVersion,
//      "org.lwjgl.lwjgl" % "lwjgl" % lwjglVersion,
//      "org.lwjgl.lwjgl" % "lwjgl-platform" % lwjglVersion,
      "org.lwjgl.lwjgl" % "lwjgl_util" % lwjglVersion //FIXME; shouldn't need
  )).
  jvmSettings(lwjglSettings: _*).
  jvmConfigure(
    _.enablePlugins(LWJGLPlugin)
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val BlockSmithJVM = BlockSmith.jvm
lazy val BlockSmithJS = BlockSmith.js

