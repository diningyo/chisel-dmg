import chisel3._
import org.scalatest._
import chiseltest._

object Inst171DECRP extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x12)

    // dec bc                      ; bc = $0000
    compareReg(0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x0000, 0x0162, false, false, false, false)
    compareReg(0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x0000, 0x0163, false, false, false, false)

	  // dec de                      ; de = $0000
    compareReg(0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x0000, 0x0163, false, false, false, false)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x0000, 0x0164, false, false, false, false)

	  // dec hl                      ; hl = $0000
    compareReg(0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x0000, 0x0164, false, false, false, false)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0000, 0x0165, false, false, false, false)

    // dec sp                      ; sp = $0000
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0000, 0x0165, false, false, false, false)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false)
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x0167, false, false, false, false)

    // ld b, $01                   ; b  = $01
    dut.clock.step(2)
    // dec bc                      ; bc = $00ff
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x016a, false, false, false, false)
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x016b, false, false, false, false)
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x016b, false, false, false, false)
    compareReg(0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x016c, false, false, false, false)

    // ld d, $00                   ; d = $00
    // ld e, $00                   ; e = $00
    // dec de                      ; de = $ffff
    dut.clock.step(2)
    compareReg(0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x016f, false, false, false, false)
    compareReg(0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x0170, false, false, false, false)
    compareReg(0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0xffff, 0x0170, false, false, false, false)
    compareReg(0x00, 0x00, 0xff, 0xff, 0xff, 0x00, 0x00, 0xffff, 0x0171, false, false, false, false)
  }
}
