// See README.md for license details.

ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "diningyo"

lazy val genTestData = taskKey[Unit]("Generate test data for this project")

genTestData := {
  import sys.process.urlToProcess
  import java.net.URL
  import java.io.File

  val saveDir = ""
  val siteUrl ="https://gbdev.gg8.se/files/roms/blargg-gb-tests"

  val f = new File(saveDir + "test.zip")

  new URL(siteUrl + "/cgb_sound.zip") #> f !!
}

lazy val root = (project in file("."))
  .settings(
    name := "chisel-dmg",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.4.1",
      "edu.berkeley.cs" %% "chisel-iotesters" % "1.5.1",
      "edu.berkeley.cs" %% "chiseltest" % "0.3.1" % "test"
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.4.1" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  )
