import chisel3._
import chisel3.util._

class xilinx_mem(val hex_path: String)
    extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    // external
    val clk = Input(Clock())

   // memory
    val addr = Input(UInt(16.W))
    val q = Output(UInt(8.W))
    val ren = Input(Bool())
    val wen = Input(Bool())
    val data = Input(UInt(8.W))
  })

  addResource("/mem/xilinx_mem.sv")
}
