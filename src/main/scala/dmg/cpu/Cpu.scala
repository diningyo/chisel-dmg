import chisel3._
import chisel3.util._


class CpuIO extends Bundle {
  val addr = UInt(16.W)
  val attr = Bool()
  val data = UInt(8.W)
}

class Cpu extends Module {
  val io = IO(new CpuIO())

  io := DontCare
}
