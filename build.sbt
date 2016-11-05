import LWJGLPlugin._

name := "BlockSmith"

scalaVersion in ThisBuild := "2.12.1"

val slickVersion = "1.0.2"
val lwjglVersion = "2.9.3"
val reactorsVersion = "0.8"

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
    wartremoverWarnings ++= Warts.unsafe,
    wartremoverErrors ++= Seq(Wart.Return),
    name := "BlockSmith cross project",
    description := "BlockSmith is currently an experiment in the ways of MineCraft.",
    version := "0.1.0-SNAPSHOT",
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at
        "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at
        "https://oss.sonatype.org/content/repositories/releases"
    ),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.8.9" % "it,test"
    )

  ).
  jvmSettings(
    // Add JVM-specific settings here
      libraryDependencies ++= Seq(
      "org.slick2d" % "slick2d-core" % slickVersion
//      ,"org.lwjgl.lwjgl" % "lwjgl" % lwjglVersion,
//      ,"org.lwjgl.lwjgl" % "lwjgl-platform" % lwjglVersion,
        ,"org.lwjgl.lwjgl" % "lwjgl_util" % lwjglVersion //FIXME; shouldn't need
        // ,"io.reactors" %% "reactors" % reactorsVersion //TODO: Not used yet
        // ,"com.storm-enroute" %% "macrogl" % "0.4-SNAPSHOT" //TODO: Not used yet
)).
  jvmSettings(lwjglSettings: _*).
  jvmConfigure(
    _.enablePlugins(LWJGLPlugin)
  ).
  jsSettings(
    // Add JS-specific settings here
    libraryDependencies ++= Seq(
      // "io.reactors" %%% "reactors" % reactorsVersion //TODO: Not used yet
      // ,"com.storm-enroute" %%% "macrogl" % "0.4-SNAPSHOT"  //FIXME, not available
    )
  )

lazy val BlockSmithJVM = BlockSmith.jvm
lazy val BlockSmithJS = BlockSmith.js

