import chisel3._
import chisel3.util._


class CpuIO extends Bundle {

}

class Cpu extends Module {
  val io = IO(new CpuIO())
}
