import chisel3._
import chisel3.util._
import Chisel.throwException

class MemIO extends Bundle {
  val addr = Output(UInt(16.W))
  val wen = Output(Bool())
  val wrdata = Output(UInt(8.W))
  val rddata = Input(UInt(8.W))
}

sealed trait MemType
case object ChiselMem extends MemType
case object Xilinx extends MemType
case object Intel extends MemType

class Mem(
  val hexPath: String = "",
  val memType: MemType = Xilinx,
) extends Module {

  val io = IO(Flipped(new MemIO()))

  io := DontCare

  memType match {
    case ChiselMem => true.B
    case Xilinx =>
      val mem = Module(new xilinx_mem(hexPath))
      mem.io.clk  := clock
      mem.io.addr := io.addr
      io.rddata   := mem.io.q
      mem.io.ren  := !io.wen
      mem.io.wen  := io.wen
      mem.io.data := io.wrdata

    case Intel => throwException("Not support yet")
  }
}
