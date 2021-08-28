import chisel3._
import org.scalatest._
import chiseltest._

object Inst300SCFCCF extends FlatSpec with ChiselScalatestTester with TestUtil {
  def apply(implicit dut: CpuTestTb) {
    // test code starts from $0150.
    dut.clock.step(0x50)

    // skip register initialization.
    dut.clock.step(0x12)

    // ;; scf : c = 1
    // ld  a, $00
    // add a, $00                  ; set z flag
    dut.clock.step(0x4)

    // scf                         ; z = 1 / c = 1
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0166, false, false, false, false) // fetch
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0167, true,  false, false, false) // execute
    compareReg(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0168, true,  false, false, true)  // check


    // ld  a, $0f
    // ld  b, $01
    // add a, $01                  ; set h flag
    // sub a, b                    ; set n flag
    dut.clock.step(0x5)

    // scf
    compareReg(0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016e, false, false, true,  false) // fetch
    compareReg(0x0f, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x016f, false, true,  true,  false) // execute
    compareReg(0x0f, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0170, false, false, false, true)  // check

    // ;; ccf : c = c ^ 1
    // ld  a, $00
    // add a, $00                  ; set z flag
    dut.clock.step(0x2)

    // ccf                         ; z = 1 / c = 1
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0173, false, false, false, true)  // fetch
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0174, true,  false, false, false) // execute
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x0175, true,  false, false, true)  // check

    // ld  a, $ff
    // ld  b, $01
    // add a, $01                  ; set h flag
    // sub a, b                    ; set n flag
    dut.clock.step(0x5)

    // ccf
    compareReg(0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017b, true,  false, true,  true)  // fetch
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017c, false, true,  true,  true)  // execute
    compareReg(0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0000, 0x017d, false, false, false, false) // check
  }
}
