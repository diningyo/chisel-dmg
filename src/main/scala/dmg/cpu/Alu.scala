import chisel3._
import chisel3.stage._
import chisel3.util._
import Chisel.chiselMain


class Alu extends Module {
  val io = IO(new Bundle {
    val decoded = ValidIO(new DecodedInst)
  })
}

object Alu extends App {
  val name = "Alu"
  val rtl = (new ChiselStage).emitVerilog(
      new InstDecoder,
      Array(
    "-td=rtl", s"-o=$name"
      ))

  println(rtl)
}
