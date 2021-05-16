import chisel3._
import chisel3.util._


class DebugIO extends Bundle {
  val finish = Output(Bool())
  val success = Output(Bool())
}


class Dmg(
  val hex_path: String,
  val is_debug: Boolean = true) extends Module {
  val io = IO(new Bundle {
    val debug = if (is_debug) Option(new DebugIO()) else None
  })

  io := DontCare


  val mem = Module(new Mem(hex_path))

  mem.io := DontCare

  if (is_debug) {
    val dbg = io.debug.get
    dbg.finish := false.B
    dbg.success := false.B
  }
}
