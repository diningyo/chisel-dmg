// See README.md for license details.

ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "diningyo"

lazy val getTestData = taskKey[Unit]("Get test data for this project")

/**
  *
  *
  * @return
  */
getTestData := {
  import sys.process.urlToProcess
  import java.net.URL
  import java.io.File

  val saveDir = ""
  val siteUrl ="https://gbdev.gg8.se/files/roms/blargg-gb-tests"

  val f = new File(saveDir + "test.zip")

  new URL(siteUrl + "/cgb_sound.zip") #> f !!
}

lazy val convertBin2Hex = taskKey[Unit]("Convert test binary to Hex data")

convertBin2Hex := {
  import java.io.PrintWriter
  import java.io.File
  import java.nio.file.{Files, Paths}
  import scala.util.matching.Regex
  import scala.sys.process._

  val path = "src/test/resources/cpu"
  val fileList = (new File(path)).listFiles()

  fileList.withFilter { file =>
    // get file extension
    val fileName = file.getName()
    val extenstion = try {
      Some(fileName.substring(fileName.lastIndexOf(".")))
    } catch {
      case _: IndexOutOfBoundsException => None
    }

    if (extenstion.getOrElse("") == ".s") true else false
  }.foreach { testSourcefile =>
    // build test ROM code
    val workDir = testSourcefile.getParent
    val romObjPath = s"${workDir}/test.o"

    val romObj = new File(romObjPath)

    if (romObj.exists == true) {
      println(s"${romObj.getPath} is exists, so remove it")
      romObj.delete
    }

    // compile
    val wlaBuildResult = s"wla-gb -o ${romObjPath} ${testSourcefile.getPath}" !!

    // link
    val linkFilePath = s"${workDir}/linkfile"
    val gbFilePath = s"${workDir}/${testSourcefile.getName}.gb"
    println(s"link = ${linkFilePath}")
    println(s"gbFilePath = ${gbFilePath}")
    val wlaLinkResult = s"wlalink -d -v -S ${linkFilePath} ${gbFilePath}" !!

    // convert binary to hex
    val byteArray = Files.readAllBytes(Paths.get(gbFilePath))
    val outPath = gbFilePath + ".hex"
    val pw = new PrintWriter(outPath)

    println(s"hexFilePath = ${outPath}")
    byteArray.zipWithIndex.foreach {
      case (i, idx) => {
        val sep = if ((idx % 0x10) == 0xf) "\n" else " "
        pw.write(f"$i%02x${sep}")
      }
    }

    pw.close()
  }
}

lazy val root = (project in file("."))
  .settings(
    name := "chisel-dmg",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.4.3",
      "edu.berkeley.cs" %% "chisel-iotesters" % "1.5.3",
      "edu.berkeley.cs" %% "chiseltest" % "0.3.3" % "test"
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.4.3" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  )
