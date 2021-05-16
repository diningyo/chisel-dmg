import chisel3._
import chisel3.stage._
import chisel3.util._
import Chisel.chiselMain


class Ifu extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(8.W))
    val decoded = ValidIO(new DecodedInst)
  })
}

object Ifu extends App {
  val name = "Alu"
  val rtl = (new ChiselStage).emitVerilog(
      new InstDecoder,
      Array(
    "-td=rtl", s"-o=$name"
      ))

  println(rtl)
}
