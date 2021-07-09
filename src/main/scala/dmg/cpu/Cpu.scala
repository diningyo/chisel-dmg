import chisel3._
import chisel3.util._


class CpuIO extends Bundle {
  val mem = new MemIO()
}

class Cpu extends Module {
  val io = IO(new CpuIO())

  io := DontCare

  val ifu = Module(new Ifu())
  val idu = Module(new InstDecoder())
  val alu = Module(new Alu())

  io.mem <> ifu.io.mem

  idu.io.inst := ifu.io.inst.bits


}
