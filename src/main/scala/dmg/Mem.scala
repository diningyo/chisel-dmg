import chisel3._
import chisel3.util._

class MemIO extends Bundle {
  val addr = Input(UInt(16.W))
  val attr = Input(Bool())
  val wrdata = Input(UInt(8.W))
  val rddata = Output(UInt(8.W))
}

sealed trait MemType
case object ChiselMem extends MemType
case object Xilinx extends MemType

class Mem(
  val hex_path: String = "",
  val mem_type: MemType = Xilinx,
) extends Module {

  val io = IO(new MemIO())

  mem_type match {
    //case ChiselMem => true.B
    case Xilinx => true.B
      val mem = Module(new XilinxMem(hex_path))
  }
}
