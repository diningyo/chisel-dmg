import chisel3._
import chiseltest._

object Inst01LD extends TestUtil {
  def apply(implicit dut: CpuTestTb) {
    dut.clock.setTimeout(100)

    compareReg(0, 0, 0, 0, 0, 0, 0, 0, 0x100, false, false, false, false)
    dut.clock.step(1)

    // 1cycleごとに期待値を比較していく
    // NOTE: 初期値どうしよう。。bgbの値に合わせても良いのかも。
    // ld a, $a5                  ; a = $a5
    // ld a, imm -> need 2 cycles
    //            a     b     c     d     e     h     l    sp     pc    f_z    f_n    f_h    f_c
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x101, false, false, false, false)
    dut.clock.step(1)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x102, false, false, false, false)
    dut.clock.step(1)
    compareReg(0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x103, false, false, false, false)
    dut.clock.step(1)

  	// ld b, a
    compareReg(0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x104, false, false, false, false)
    dut.clock.step(1)

  	// ld c, b
    compareReg(0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x105, false, false, false, false)
    dut.clock.step(1)

  	// ld d, c
    compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x106, false, false, false, false)
    dut.clock.step(1)

  	// ld e, d
    compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x00, 0x107, false, false, false, false)
    dut.clock.step(1)

  	// ld h, e
    compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x00, 0x108, false, false, false, false)
    dut.clock.step(1)

  	// ld l, h
    compareReg(0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0xa5, 0x00, 0x109, false, false, false, false)
    dut.clock.step(1)
  }
}
