import chisel3._
import chiseltest._

trait TestUtil {
  def compareReg(
    a: Int, b: Int, c: Int, d: Int, e: Int,
    h: Int, l: Int, sp: Int, pc: Int,
    f_z: Boolean, f_n: Boolean, f_h: Boolean, f_c: Boolean,
    cycles: Int = 1
  )(implicit dut: CpuTestTb): Unit = {
    println(s"${dut.io.regs.peek}")

    dut.io.regs.a.read.expect(a.U)
    dut.io.regs.b.read.expect(b.U)
    dut.io.regs.c.read.expect(c.U)
    dut.io.regs.d.read.expect(d.U)
    dut.io.regs.e.read.expect(e.U)
    dut.io.regs.h.read.expect(h.U)
    dut.io.regs.l.read.expect(l.U)
    dut.io.regs.sp.read.expect(sp.U)
    dut.io.regs.pc.read.expect(pc.U)

    // flagはとりあえず各ビット単位で比較
    dut.io.regs.f.z.expect(f_z.B)
    dut.io.regs.f.n.expect(f_n.B)
    dut.io.regs.f.h.expect(f_h.B)
    dut.io.regs.f.c.expect(f_c.B)

    dut.clock.step(cycles)
  }
}
