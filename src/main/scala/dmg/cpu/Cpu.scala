import chisel3._
import chisel3.util._

class CpuIO extends Bundle {
  val mem = new MemIO()
}

/**
  * Base class for CPU register
  */
trait BaseCpuReg extends Bundle {
  // NOTE: these methods must be implemented in derived class.
  def write(data: UInt)
  def read(): UInt
}

/**
  * General Purpose register
  *
  * @param bits : numbers of register bit width
  */
class GP(bits: Int) extends BaseCpuReg {
  val data = UInt(bits.W)

  def write(wr_val: UInt): Unit = data := wr_val
  def read(): UInt = data
}

class PC(bits: Int) extends GP(bits) {
  def inc: Unit = data + 1.U
}

class F extends Bundle with BaseCpuReg {
  val z = Bool()
  val n = Bool()
  val h = Bool()
  val c = Bool()

  def write(data: UInt): Unit = {
    z := data(7)
    n := data(6)
    h := data(5)
    c := data(4)
  }
  def read(): UInt = Cat(z, n, h, c, 0.U(4.W))
}

class CpuReg extends Bundle {
  val a = new GP(8)
  val f = new F
  val b = new GP(8)
  val c = new GP(8)
  val d = new GP(8)
  val e = new GP(8)
  val h = new GP(8)
  val l = new GP(8)
  val sp = new GP(16)
  val pc = new PC(16)

  // methods for 16 bit access
  def af: UInt = Cat(a.read(), f.read())
  def bc: UInt = Cat(b.read(), c.read())
  def de: UInt = Cat(d.read(), e.read())
  def hl: UInt = Cat(h.read(), l.read())
}

class Cpu extends Module {
  val io = IO(new CpuIO)

  io := DontCare

  // declare registers
  val w_reg_init_val = WireInit(0.U.asTypeOf(new CpuReg))
  w_reg_init_val.pc := 0x100.U
  val r_regs = RegInit(w_reg_init_val)

  // increment PC.
  r_regs.pc.inc

  io.mem.addr := r_regs.pc
  io.mem.wen := false.B
}
