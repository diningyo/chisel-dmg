import java.nio.file.{Files, Paths}

import chisel3._

import org.scalatest._
import chiseltest._
import chiseltest.internal.VerilatorBackendAnnotation
import chiseltest.experimental.TestOptionBuilder._

class MemTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Mem"

  val testName = "01-special"
  val annos = Seq(VerilatorBackendAnnotation)

  def write(addr: BigInt, data: BigInt)(implicit c: Mem): Unit = {
    c.io.wen.poke(true.B)
    c.io.addr.poke(addr.U)
    c.io.wrdata.poke(data.U)
    c.clock.step(1)
  }

  def read(addr: BigInt, exp: BigInt)(implicit c: Mem): Unit = {
    c.io.wen.poke(false.B)
    c.io.addr.poke(addr.U)
    c.clock.step(1)
    c.io.rddata.expect(exp.U)
    c.clock.step(1)
  }

  //it should f"be able to write/read" in {
  //  test(new Mem(testName)).withAnnotations(annos) { implicit c =>
  //    write(100, 100)
  //    read(100, 100)
  //    c.clock.step(10)
  //  }
  //}

  it should f"be able to read ${testName} data as initial data." in {
    test(new Mem("src/test/resources/blargg-gb-tests/cpu_instrs/individual/01-special.hex")).withAnnotations(annos) { implicit c =>
      val f = "src/test/resources/blargg-gb-tests/cpu_instrs/individual/01-special.gb"
      val byteArray = Files.readAllBytes(Paths.get(f))

      byteArray.zipWithIndex.foreach {
        case (byte, addr) =>
          read(addr, byte & 0xff)
          c.clock.step(10)
      }
    }
  }
}
