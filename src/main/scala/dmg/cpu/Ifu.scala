import chisel3._
import chisel3.stage._
import chisel3.util._
import Chisel.chiselMain

object IfuData {
  val initPC = 100.U(16.W)
}

class Ifu extends Module {
  val io = IO(new Bundle {
    val mem = new MemIO()
    val inst = ValidIO(UInt(8.W))
  })

  io := DontCare

  val pc = RegInit(IfuData.initPC)

  pc := pc + 1.U;

  io.mem.addr := pc
  io.mem.wen := false.B

  io.inst.valid := true.B
  io.inst.bits := io.mem.rddata
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
