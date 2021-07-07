import java.io.PrintWriter
import java.nio.file.{Files, Paths}

object Convert extends App {
  val path = "src/test/scala/resources/blargg-gb-tests/cpu_instrs/individual/01-special.gb"
  val byteArray = Files.readAllBytes(Paths.get(path))

  byteArray.slice(0, 1023).foreach(i => println(f"$i%02x"))

  val outPath = "src/test/scala/resources/blargg-gb-tests/cpu_instrs/individual/01-special.hex"
  val pw = new PrintWriter(outPath)

  byteArray.zipWithIndex.foreach {
    case (i, idx) => {
      println(idx)
      pw.write(f"$i%02x")

      if ((idx % 0x10) == 0xf) {
        pw.write(f"\n")
      } else {
        pw.write(f" ")
      }
    }
  }
}
