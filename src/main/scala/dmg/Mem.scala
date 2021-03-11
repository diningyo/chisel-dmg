import chisel3._
import chisel3.util._

class MemIO extends Bundle {
  val addr = Input(UInt(16.W))
  val attr = Input(Bool())
  val wrdata = Input(UInt(8.W))
  val rddata = Output(UInt(8.W))
}

class Mem extends Module {
  val io = IO(new MemIO())
}
