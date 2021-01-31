import chisel3._
import chisel3.util._


class CpuIO extends Bundle {
  val mem = Flipped(new MemIO())
}

class Cpu extends Module {
  val io = IO(new CpuIO())

  io := DontCare
}
